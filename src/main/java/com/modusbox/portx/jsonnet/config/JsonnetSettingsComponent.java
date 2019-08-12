package com.modusbox.portx.jsonnet.config;

import com.intellij.openapi.components.*;
import org.jetbrains.annotations.NotNull;

@State(name = "JsonnetSettings", storages = {@Storage("jsonnet-settings.xml")})
public class JsonnetSettingsComponent implements PersistentStateComponent<JsonnetSettings>, ApplicationComponent {
    private JsonnetSettings settings = new JsonnetSettings();

    public JsonnetSettings getState() {
        return settings;
    }

    public void loadState(@NotNull JsonnetSettings state) {
        this.settings = state;
    }

    public void projectOpened() {
    }

    public void projectClosed() {
    }

    @NotNull
    public String getComponentName() {
        return "JsonnetSettings";
    }

    public void initComponent() {
    }

    public void disposeComponent() {
        this.settings = null;
    }
}