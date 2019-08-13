package com.modusbox.portx.jsonnet.config;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class JsonnetConfigurable implements SearchableConfigurable, Configurable.NoScroll, Disposable {

    private final JsonnetSettingsComponent mySettingsComponent;
    private final JsonnetProjectSettingsComponent myProjectSettingsComponent;

    private JsonnetSettingsPanel myPanel;

    public JsonnetConfigurable(Project project) {
        mySettingsComponent = ServiceManager.getService(JsonnetSettingsComponent.class);
        myProjectSettingsComponent = ServiceManager.getService(project, JsonnetProjectSettingsComponent.class);
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Jsonnet";
    }

    @NotNull
    @Override
    public String getId() {
        return "jsonnet";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        myPanel = new JsonnetSettingsPanel();
        return myPanel.createPanel(mySettingsComponent, myProjectSettingsComponent);
    }

    @Override
    public boolean isModified() {
        return myPanel.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        myPanel.apply();
    }
    @Override
    public void reset() {
        myPanel.reset();
    }

    @Override
    public void disposeUIResources() {
        Disposer.dispose(this);
    }

    @Override
    public void dispose() {
        myPanel = null;
    }
}
