package io.portx.datasonnet.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

@State(name = "DataSonnetProjectSettings", storages = {@Storage("dataSonnet-settings.xml")})
public class DataSonnetProjectSettingsComponent implements PersistentStateComponent<DataSonnetProjectSettings> {
    private DataSonnetProjectSettings settings = new DataSonnetProjectSettings();

    @NotNull
    public DataSonnetProjectSettings getState() {
        return settings;
    }

    public void loadState(@NotNull DataSonnetProjectSettings state) {
        this.settings = state;
    }

    public void projectOpened() {
    }

    public void projectClosed() {
    }

    @NotNull
    public String getComponentName() {
        return "DataSonnetProjectSettings";
    }

    /**
     * Gets the {@link DataSonnetProjectSettings} settings for the specified project.
     *
     * @param theProject The project for which to get the DataSonnet  settings.
     * @return The {@link DataSonnetProjectSettings} settings for the specified project.
     */
    public static DataSonnetProjectSettings getSettings(@NotNull final Project theProject) {
        final DataSonnetProjectSettingsComponent projectSettings = theProject.getService(DataSonnetProjectSettingsComponent.class);
        if (projectSettings != null) {
            return projectSettings.getState();
        }

        return new DataSonnetProjectSettings();
    }

}