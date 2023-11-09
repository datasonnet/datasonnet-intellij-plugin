package io.portx.datasonnet.debug.runner;

import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.openapi.util.NotNullLazyValue;
import io.portx.datasonnet.language.DataSonnetIcon;

public class DataSonnetRunConfigurationType extends ConfigurationTypeBase {
    static final String ID = "DataSonnetRunConfiguration";

    protected DataSonnetRunConfigurationType() {
        super(ID, "DataSonnet", "DataSonnet run configuration type",
                NotNullLazyValue.createValue(() -> DataSonnetIcon.FILE));
        addFactory(new DataSonnetConfigurationFactory(this));
    }

}
