package io.portx.datasonnet.engine;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ScenarioManager {

    String INTEGRATION_TEST_FOLDER_NAME = "mapping-tests";
    String INTEGRATION_TEST_FOLDER_PATH = "src/test/resources/mapping-tests";
    String NO_SCENARIO = "No scenario";
    String DEFAULT_SCENARIO_NAME = "default_scenario";

    @NotNull
    static ScenarioManager getInstance(Project myProject) {
        return myProject.getService(ScenarioManager.class);
    }

    @Nullable Scenario createScenario(PsiFile psiFile, String scenarioName);

    VirtualFile findOrCreateMappingTestFolder(PsiFile psiFile);

    @Nullable VirtualFile findMappingTestFolder(PsiFile psiFile);

    @Nullable VirtualFile createMappingTestFolder(PsiFile dataSonnetFile);

    @Nullable VirtualFile getScenariosRootFolder(PsiFile dataSonnetFile);

    @NotNull List<Scenario> getScenariosFor(PsiFile dataSonnetMappingFile);

    @Nullable Scenario findScenario(@NotNull VirtualFile dsMappingFile, @NotNull String scenarioName);

    void setCurrentScenario(String dataSonnetMappingName, Scenario scenario);

    Scenario getCurrentScenario(String dataSonnetMappingName);

}
