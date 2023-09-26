package io.portx.datasonnet.config;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DataSonnetConfigurable implements SearchableConfigurable, Configurable.NoScroll, Disposable {

    private final DataSonnetSettingsComponent mySettingsComponent;
    private final DataSonnetProjectSettingsComponent myProjectSettingsComponent;

    private DataSonnetSettingsPanel myPanel;

    public DataSonnetConfigurable(Project project) {
        mySettingsComponent = ServiceManager.getService(DataSonnetSettingsComponent.class);
        myProjectSettingsComponent = ServiceManager.getService(project, DataSonnetProjectSettingsComponent.class);
    }

    //@Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Datasonnet";
    }

    @NotNull
    @Override
    public String getId() {
        return "datasonnet";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        myPanel = new DataSonnetSettingsPanel();
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
