package io.portx.datasonnet.engine;

import com.datasonnet.Mapper;
import com.datasonnet.MapperBuilder;
import com.datasonnet.debugger.DataSonnetDebugger;
import com.datasonnet.document.DefaultDocument;
import com.datasonnet.document.Document;
import com.datasonnet.document.MediaType;
import com.datasonnet.document.MediaTypes;
import com.datasonnet.spi.Library;
import com.intellij.codeInspection.AbstractDependencyVisitor;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import io.portx.datasonnet.config.DataSonnetProjectSettingsComponent;
import io.portx.datasonnet.util.ClasspathUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Paths;
import java.util.*;

public class DataSonnetEngine {

    private static final Logger LOGGER = Logger.getInstance(DataSonnetEngine.class);

    private final VirtualFile mappingFile;
    private final Scenario scenario;
    private final Project project;
    private final MediaType outputMimeType;
    private boolean isDebug = false;

    private DataSonnetDebugger dataSonnetDebugger;


    public DataSonnetEngine(@NotNull Project project, @NotNull String scriptPath, @NotNull String scenario, @NotNull String outputMimeTypeName, boolean isDebug) {
        this.project = project;
        this.outputMimeType = MediaType.valueOf(outputMimeTypeName);
        this.isDebug = isDebug;
        this.mappingFile = LocalFileSystem.getInstance().findFileByPath(scriptPath);
        this.scenario = ScenarioManager.getInstance(project).findScenario(mappingFile, scenario);

        if (isDebug) {
            dataSonnetDebugger = DataSonnetDebugger.getDebugger();
        }
    }

    public DataSonnetEngine(@NotNull Project project, @NotNull VirtualFile mappingFile, @NotNull Scenario scenario, @NotNull MediaType outputMimeType) {
        this.project = project;
        this.scenario = scenario;
        this.outputMimeType = outputMimeType;
        this.isDebug = false;
        this.mappingFile = mappingFile;
    }

    public com.datasonnet.document.Document runDataSonnetMapping() {
        com.intellij.openapi.editor.Document document = ApplicationManager.getApplication().runReadAction((Computable<com.intellij.openapi.editor.Document>) () -> FileDocumentManager.getInstance().getDocument(mappingFile));
        String mappingScript = document.getText();

        String camelFunctions = "local cml = { exchangeProperty(str): exchangeProperty[str], header(str): header[str], properties(str): properties[str] };\n";
        String dataSonnetScript = camelFunctions + mappingScript;

        String payload = "{}";

        Map<String, VirtualFile> inputFiles = scenario.getInputFiles();
        HashMap<String, Document<?>> variables = new HashMap<>();

        MediaType payloadMimeType = MediaTypes.APPLICATION_JSON;

        for (Map.Entry<String, VirtualFile> f : inputFiles.entrySet()) {
            String contents = null;
            try {
                contents = new String(f.getValue().contentsToByteArray());
                if (f.getKey().equals("payload")) {
                    payload = contents;
                    payloadMimeType = MediaTypes.forExtension(f.getValue().getExtension()).get();
                } else {
                    variables.put(f.getKey(), new DefaultDocument<>(contents, MediaTypes.forExtension(f.getValue().getExtension()).get()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Map<String, String> libraries = ApplicationManager.getApplication().runReadAction((Computable<Map>) () -> getDSLibraries());

        try {
            ClassLoader currentCL = Thread.currentThread().getContextClassLoader();
            ClassLoader projectClassLoader = ClasspathUtils.getProjectClassLoader(project, this.getClass().getClassLoader());
            Thread.currentThread().setContextClassLoader(projectClassLoader);

            MapperBuilder builder = new MapperBuilder(dataSonnetScript).withImports(libraries).withInputNames(variables.keySet());

            try {
                for (Class clazz : scanLibraries()) {
                    Library lib = null;
                    LOGGER.info("Loading DataSonnet library: " + clazz.getName());
                    try { //First see if it's a static Scala class
                        lib = (Library) clazz.getDeclaredField("MODULE$").get(null);
                    } catch (Exception e) { //See if it has defaut constructor
                        LOGGER.info("Datasonnet library is not static Scala class. Calling Constructor");
                        try {
                            Constructor constructor = clazz.getDeclaredConstructor();
                            lib = (Library) constructor.newInstance();
                        } catch (Exception e2) {
                            LOGGER.info("Error creating Java DataSonnet library instance: " + e2.getMessage());
                            lib = null;
                        }
                    }
                    if (lib != null) {
                        LOGGER.info("Adding library: " + lib.getClass().getName());
                        builder = builder.withLibrary(lib);
                    }
                }
            } catch (Exception e) {

            }

            Mapper mapper = builder.build();
            if (isDebug) {
                getDebugger().attach();
                String[] lines = mappingScript.trim().split("\n|\r|\r\n");
                getDebugger().setLineCount(lines.length);
            }
            com.datasonnet.document.Document transformDoc = mapper.transform(new DefaultDocument<>(payload, payloadMimeType), variables, outputMimeType);
            if (isDebug) {
                getDebugger().detach();
            }

            Thread.currentThread().setContextClassLoader(currentCL);

            return transformDoc;
        } catch (Exception e) {
            return new DefaultDocument(e.getMessage() != null ? e.getMessage() : e.toString(), MediaTypes.TEXT_PLAIN);
        }
    }

    @NotNull
    private Map<String, String> getDSLibraries() {
        Map<String, String> libraries = new HashMap();

        //Search in all scopes of the project
        Collection<VirtualFile> libs = FilenameIndex.getAllFilesByExt(project, "libsonnet", GlobalSearchScope.allScope(project));
        for (VirtualFile nextLib : libs) {
            try {
                String content = VfsUtil.loadText(nextLib);
                String path = nextLib.getPath();
                if (path.toLowerCase().contains(".jar!")) {
                    path = path.substring(path.lastIndexOf("!") + 1);
                } else {
                    com.intellij.openapi.module.Module module = ModuleUtil.findModuleForFile(mappingFile, project);
                    List<VirtualFile> roots = new ArrayList(Arrays.asList(ModuleRootManager.getInstance(module).getSourceRoots()));
                    roots.addAll(Arrays.asList(ModuleRootManager.getInstance(module).getContentRoots()));

                    for (VirtualFile root : roots) {
                        if (path.startsWith(root.getPath())) {
                            path = path.replace(root.getPath(), "");
                            break;
                        }
                    }
                }
                if (path.startsWith("/")) {
                    path = path.substring(1);
                }
                libraries.put(path, content);

                //Also put another copy with relative path
                String relativePath = Paths.get(mappingFile.getParent().getPath()).relativize(Paths.get(nextLib.getPath())).toString();
                libraries.put(relativePath, content);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Search in additional paths
        DataSonnetProjectSettingsComponent projectSettings = project.getService(DataSonnetProjectSettingsComponent.class);
        List<String> libraryPaths = projectSettings.getState().getDataSonnetLibraryPaths();
        for (String libPath : libraryPaths) {
            VirtualFile libDir = VfsUtil.findFile(Paths.get(libPath), true);

            VfsUtilCore.iterateChildrenRecursively(libDir, null, fileOrDir -> {
                if (!fileOrDir.isDirectory() && fileOrDir.getExtension() != null &&
                        "libsonnet".equals(fileOrDir.getExtension().toLowerCase())) {
                    try {
                        String content = VfsUtil.loadText(fileOrDir);
                        String relativePath = Paths.get(libPath).relativize(Paths.get(fileOrDir.getPath())).toString();
                        libraries.put(relativePath, content);
                        libraries.put(fileOrDir.getPath(), content);
                    }
                    catch (IOException e) {
                        return false;
                    }
                }
                return true;
            });
        }
        return libraries;
    }

    private List<Class<?>> scanLibraries() {
        try {
            ClassLoader projectClassLoader = ClasspathUtils.getProjectClassLoader(project, this.getClass().getClassLoader());

            ScanResult scanResult = new ClassGraph().enableAllInfo().overrideClassLoaders(projectClassLoader).scan();
            ClassInfoList libs = scanResult.getSubclasses("com.datasonnet.spi.Library").filter(classInfo -> classInfo.isPublic() && !classInfo.isAbstract() && !classInfo.getName().endsWith(".CML") && //Exclude Camel library
                    !"com.datasonnet".equals(classInfo.getPackageName())); //Exclude default Datasonnet libraries
            LOGGER.info("Found " + libs.size() + " Datasonnet libraries");
            return libs.loadClasses();
        } catch (Exception e) {
            LOGGER.error("Error scanning libraries: " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public void attach() {
        DataSonnetDebugger.getDebugger().attach();
    }

    public void detach() {
        DataSonnetDebugger.getDebugger().detach();
    }

    public VirtualFile getMappingFile() {
        return mappingFile;
    }

    public DataSonnetDebugger getDebugger() {
        return dataSonnetDebugger;
    }
}
