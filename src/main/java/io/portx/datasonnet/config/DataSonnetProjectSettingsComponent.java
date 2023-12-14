package io.portx.datasonnet.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

@State(name = "DataSonnetProjectSettings", storages = {@Storage("dataSonnet-settings.xml")})
public class DataSonnetProjectSettingsComponent implements PersistentStateComponent<DataSonnetProjectSettings> {
    private DataSonnetProjectSettings settings = new DataSonnetProjectSettings();

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

}