package io.portx.datasonnet.debug.runner;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.components.BaseState;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DataSonnetConfigurationFactory extends ConfigurationFactory {

    protected DataSonnetConfigurationFactory(ConfigurationType type) {
        super(type);
    }

    @Override
    public @NotNull String getId() {
        return DataSonnetRunConfigurationType.ID;
    }

    @NotNull
    @Override
    public RunConfiguration createTemplateConfiguration(
            @NotNull Project project) {
        return new DataSonnetRunConfiguration(project, this, "DataSonnet");
    }

    @Nullable
    @Override
    public Class<? extends BaseState> getOptionsClass() {
        return DataSonnetRunConfigurationOptions.class;
    }
}
