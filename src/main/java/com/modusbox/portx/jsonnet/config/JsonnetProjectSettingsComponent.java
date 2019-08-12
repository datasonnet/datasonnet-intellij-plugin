package com.modusbox.portx.jsonnet.config;

import com.intellij.openapi.components.*;
import org.jetbrains.annotations.NotNull;

@State(name = "JsonnetProjectSettings", storages = {@Storage("jsonnet-settings.xml")})
public class JsonnetProjectSettingsComponent implements PersistentStateComponent<JsonnetProjectSettings>, ProjectComponent {
    private JsonnetProjectSettings settings = new JsonnetProjectSettings();

    public JsonnetProjectSettings getState() {
        return settings;
    }

    public void loadState(@NotNull JsonnetProjectSettings state) {
        this.settings = state;
    }

    public void projectOpened() {
    }

    public void projectClosed() {
    }

    @NotNull
    public String getComponentName() {
        return "JsonnetProjectSettings";
    }

    public void initComponent() {
    }

    public void disposeComponent() {
        this.settings = null;
    }
}