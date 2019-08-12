package com.modusbox.portx.jsonnet.editor;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ScenarioManager  extends AbstractProjectComponent implements Disposable {

    public static final String INTEGRATION_TEST_FOLDER_NAME = "mapping-tests";
    public static final String INTEGRATION_TEST_FOLDER_PATH = "src/test/mapping-tests";
    public static final String NO_SCENARIO = "No scenario";
    public static final String DEFAULT_SCENARIO_NAME = "default_scenario";

    private Map<String, Scenario> selectedScenariosByMapping = new HashMap<>();
    private Map<String, VirtualFile> jsonnetInputsFolders = new HashMap<>();

    protected ScenarioManager(Project project) {
        super(project);
    }

    public static ScenarioManager getInstance(Project myProject) {
        return myProject.getComponent(ScenarioManager.class);
    }

    @Nullable
    public Scenario createScenario(PsiFile psiFile, String scenarioName) {
        VirtualFile testFolder = findOrCreateMappingTestFolder(psiFile);
        try {
            VirtualFile scenarioFolder = WriteAction.compute(() -> testFolder.createChildDirectory(this, scenarioName));
            Scenario scenario = new Scenario(scenarioFolder);
            String mappingFileName = psiFile.getVirtualFile().getCanonicalPath();
            setCurrentScenario(mappingFileName, scenario);
            return scenario;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public VirtualFile findOrCreateMappingTestFolder(PsiFile psiFile) {
        VirtualFile testFolder = findMappingTestFolder(psiFile);
        if (testFolder == null) {
            testFolder = createMappingTestFolder(psiFile);
        }
        return testFolder;
    }
    @Nullable
    public VirtualFile findMappingTestFolder(PsiFile psiFile) {
        if (psiFile != null) {
            String qualifiedName = psiFile.getName();
            VirtualFile scenariosRootFolder = getScenariosRootFolder(psiFile);
            if (scenariosRootFolder != null && scenariosRootFolder.isValid()) {
                return scenariosRootFolder.findChild(qualifiedName);
            }
        }
        return null;
    }
    @Nullable
    public VirtualFile createMappingTestFolder(PsiFile jsonnetFile) {
        return WriteAction.compute(() -> {
            try {
                //TODO: handle creation of jsonnetInputs folder
                VirtualFile jsonnetInputsFolder = getScenariosRootFolder(jsonnetFile);
                String qName = jsonnetFile.getName();
                return jsonnetInputsFolder.createChildDirectory(this, qName);
            } catch (IOException e) {
                return null;
            }
        });
    }
    @Nullable
    public VirtualFile getScenariosRootFolder(PsiFile jsonnetFile) {
        final Module module = ModuleUtil.findModuleForFile(jsonnetFile.getVirtualFile(), jsonnetFile.getProject());
        if (module != null) {
            return getScenariosRootFolder(module);
        }
        return null;
    }

    @Nullable
    private VirtualFile getScenariosRootFolder(@Nullable Module module) {
        if (module == null) {
            return null;
        }
        String moduleName = module.getName();
        VirtualFile maybeFolder = jsonnetInputsFolders.get(moduleName);
        if (maybeFolder != null) {
            return maybeFolder;
        }

        ModuleRootManager rootManager = ModuleRootManager.getInstance(module);

        VirtualFile[] sourceRoots = rootManager.getSourceRoots(true);
        for (VirtualFile sourceRoot : sourceRoots) {
            if (sourceRoot.isDirectory() && sourceRoot.getName().endsWith(INTEGRATION_TEST_FOLDER_NAME)) {
                jsonnetInputsFolders.put(moduleName, sourceRoot);
                return sourceRoot;
            }
        }

        if (jsonnetInputsFolders.get(moduleName) == null) { //Need to create one
            try {
                //See if "src/test/jsonnetInputs exists, if not, create it
                VirtualFile moduleRoot = rootManager.getContentRoots()[0];
                VirtualFile testjsonnetInputs = LocalFileSystem.getInstance().findFileByIoFile(new File(rootManager.getContentRoots()[0].getCanonicalPath(), INTEGRATION_TEST_FOLDER_PATH));
                if (testjsonnetInputs == null) {
                    VirtualFile srcDir = moduleRoot.findFileByRelativePath("src");
                    if (srcDir == null)
                        srcDir = moduleRoot.createChildDirectory(this, "src");
                    VirtualFile testDir = srcDir.findFileByRelativePath("test");
                    if (testDir == null)
                        testDir = srcDir.createChildDirectory(this, "test");
                    //Create it here
                    testjsonnetInputs = testDir.createChildDirectory(this, INTEGRATION_TEST_FOLDER_NAME);
                }

                final VirtualFile jsonnetInputsFile = testjsonnetInputs;

                final Application app = ApplicationManager.getApplication();
                Runnable action = new Runnable() {
                    @Override
                    public void run() {
                        app.runWriteAction(new Runnable() {
                            @Override
                            public void run() {
                                ModifiableRootModel model = ModuleRootManager.getInstance(module).getModifiableModel();
                                ContentEntry[] entries = model.getContentEntries();
                                for (ContentEntry entry : entries) {
                                    if (entry.getFile() == moduleRoot)
                                        entry.addSourceFolder(jsonnetInputsFile, true);
                                }
                                model.commit();
                            }
                        });
                    }
                };

                if (app.isDispatchThread()) {
                    action.run();
                }
                else {
                    app.invokeAndWait(action, ModalityState.current());
                }

                jsonnetInputsFolders.put(moduleName, testjsonnetInputs);
                return testjsonnetInputs;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return null;
    }

    public void setCurrentScenario(String jsonnetMappingName, Scenario scenario) {
        this.selectedScenariosByMapping.put(jsonnetMappingName, scenario);
    }

    public Scenario getCurrentScenario(String jsonnetMappingName) {
        return selectedScenariosByMapping.get(jsonnetMappingName);
    }

    @NotNull
    public List<Scenario> getScenariosFor(PsiFile jsonnetMappingFile) {
        final List<Scenario> result = new ArrayList<>();
        final Module moduleForFile = ModuleUtil.findModuleForFile(jsonnetMappingFile.getVirtualFile(), jsonnetMappingFile.getProject());
        if (moduleForFile != null) {
            List<VirtualFile> scenarios = findScenarios(jsonnetMappingFile);
            result.addAll(scenarios.stream().map(Scenario::new).collect(Collectors.toList()));
        }
        return result;
    }

    private List<VirtualFile> findScenarios(PsiFile psiFile) {
        VirtualFile mappingTestFolder = findMappingTestFolder(psiFile);
        if (mappingTestFolder != null) {
            return Arrays.asList(mappingTestFolder.getChildren());
        }
        return new ArrayList<>();
    }

    @Override
    public void dispose() {

    }
}
