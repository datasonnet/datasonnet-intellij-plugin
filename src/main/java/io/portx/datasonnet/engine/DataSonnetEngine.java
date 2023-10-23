package io.portx.datasonnet.engine;

import com.datasonnet.Mapper;
import com.datasonnet.MapperBuilder;
import com.datasonnet.debugger.DataSonnetDebugger;
import com.datasonnet.document.DefaultDocument;
import com.datasonnet.document.Document;
import com.datasonnet.document.MediaType;
import com.datasonnet.document.MediaTypes;
import com.datasonnet.spi.Library;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.SlowOperations;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import io.portx.datasonnet.util.ClasspathUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Paths;
import java.util.*;

public class DataSonnetEngine {

    private final VirtualFile mappingFile;
    private final Scenario scenario;
    private final Project project;
    private final String outputMimeType;
    private boolean isDebug = false;

    private DataSonnetDebugger dataSonnetDebugger;

    public DataSonnetEngine(@NotNull Project project, @NotNull String script, @NotNull String scenario, @NotNull String outputMimeType, boolean isDebug) {
        this.project = project;
        this.mappingFile = LocalFileSystem.getInstance().findFileByPath(script);
        this.scenario = ScenarioManager.getInstance(project).findScenario(this.mappingFile, scenario);
        this.outputMimeType = outputMimeType;
        this.isDebug = isDebug;

        if (isDebug) {
            dataSonnetDebugger = DataSonnetDebugger.getDebugger();
        }
    }

    public String runDataSonnetMapping() {
        com.intellij.openapi.editor.Document document = ApplicationManager.getApplication().runReadAction((Computable<com.intellij.openapi.editor.Document>) () ->
                FileDocumentManager.getInstance().getDocument(mappingFile));
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

        Map<String, String> libraries = SlowOperations.allowSlowOperations(() ->
                ApplicationManager.getApplication().runReadAction((Computable<Map<String, String>>) () -> getDSLibraries())
        );

        try {
            ClassLoader currentCL = Thread.currentThread().getContextClassLoader();
            ClassLoader projectClassLoader = ClasspathUtils.getProjectClassLoader(project, this.getClass().getClassLoader());
            Thread.currentThread().setContextClassLoader(projectClassLoader);

            MapperBuilder builder = new MapperBuilder(dataSonnetScript)
                    .withImports(libraries)
                    .withInputNames(variables.keySet());

            try {
                for (Class clazz : scanLibraries()) {
                    Library lib = null;
                    try { //First see if it's a static Scala class
                        lib = (Library) clazz.getDeclaredField("MODULE$").get(null);
                    } catch (Exception e) { //See if it has defaut constructor
                        try {
                            Constructor constructor = clazz.getDeclaredConstructor();
                            lib = (Library) constructor.newInstance();
                        } catch (Exception e2) {
                            lib = null;
                        }
                    }
                    if (lib != null) {
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
            com.datasonnet.document.Document transformDoc =
                    mapper.transform(new DefaultDocument<>(payload, payloadMimeType), variables, MediaType.valueOf(outputMimeType));
            if (isDebug) {
                getDebugger().detach();
            }

            Thread.currentThread().setContextClassLoader(currentCL);

            return transformDoc.getContent().toString();
        } catch (Exception e) {
            return e.getMessage() != null ? e.getMessage() : e.toString();
        }
    }

    @NotNull
    private Map<String, String> getDSLibraries() {

        Map<String, String> libraries = new HashMap();

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

        return libraries;
    }

    private List<Class<?>> scanLibraries() {
        try {
            ClassLoader projectClassLoader = ClasspathUtils.getProjectClassLoader(project, this.getClass().getClassLoader());

            ScanResult scanResult = new ClassGraph().enableAllInfo()
                    .overrideClassLoaders(projectClassLoader)
                    .scan();
            ClassInfoList libs = scanResult.getSubclasses("com.datasonnet.spi.Library")
                    .filter(classInfo -> classInfo.isPublic() &&
                            !classInfo.isAbstract() &&
                            !classInfo.getName().endsWith(".CML") && //Exclude Camel library
                            !"com.datasonnet".equals(classInfo.getPackageName())); //Exclude default Datasonnet libraries
            return libs.loadClasses();
        } catch (Exception e) {
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
