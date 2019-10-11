package com.modusbox.portx.datasonnet.config;

import com.intellij.openapi.components.*;
import org.jetbrains.annotations.NotNull;

@State(name = "DataSonnetSettings", storages = {@Storage("dataSonnet-settings.xml")})
public class DataSonnetSettingsComponent implements PersistentStateComponent<DataSonnetSettings>, ApplicationComponent {
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

    public void initComponent() {
    }

    public void disposeComponent() {
        this.settings = null;
    }
}