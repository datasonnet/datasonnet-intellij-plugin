package io.portx.datasonnet.engine;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.java.JavaResourceRootType;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public interface ScenarioManager {

    public static final String INTEGRATION_TEST_FOLDER_NAME = "mapping-tests";
    public static final String INTEGRATION_TEST_FOLDER_PATH = "src/test/resources/mapping-tests";
    public static final String NO_SCENARIO = "No scenario";
    public static final String DEFAULT_SCENARIO_NAME = "default_scenario";

    @NotNull
    public static ScenarioManager getInstance(Project myProject) {
        return myProject.getService(ScenarioManager.class);
    }

    @Nullable
    public Scenario createScenario(PsiFile psiFile, String scenarioName);
    public VirtualFile findOrCreateMappingTestFolder(PsiFile psiFile);
    @Nullable
    public VirtualFile findMappingTestFolder(PsiFile psiFile);
    @Nullable
    public VirtualFile createMappingTestFolder(PsiFile dataSonnetFile);
    @Nullable
    public VirtualFile getScenariosRootFolder(PsiFile dataSonnetFile);
    @NotNull
    public List<Scenario> getScenariosFor(PsiFile dataSonnetMappingFile);
    @Nullable
    public Scenario findScenario(@NotNull VirtualFile dsMappingFile, @NotNull String scenarioName);
    public void setCurrentScenario(String dataSonnetMappingName, Scenario scenario);
    public Scenario getCurrentScenario(String dataSonnetMappingName);

}
