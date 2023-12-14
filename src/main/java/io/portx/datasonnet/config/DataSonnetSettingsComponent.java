package io.portx.datasonnet.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;


@Deprecated
@State(name = "DataSonnetSettings", storages = {@Storage("dataSonnet-settings.xml")})
public class DataSonnetSettingsComponent implements PersistentStateComponent<DataSonnetSettings> {
    private DataSonnetSettings settings = new DataSonnetSettings();

    public DataSonnetSettings getState() {
        return settings;
    }

    public void loadState(@NotNull DataSonnetSettings state) {
        this.settings = state;
    }

    public void projectOpened() {
    }

    public void projectClosed() {
    }

    @NotNull
    public String getComponentName() {
        return "DataSonnetSettings";
    }

}