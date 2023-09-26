package io.portx.datasonnet.debug.runner;

import com.intellij.execution.configurations.RunConfigurationOptions;
import com.intellij.openapi.components.StoredProperty;

public class DataSonnetRunConfigurationOptions extends RunConfigurationOptions {

    private final StoredProperty<String> dsScriptName =
            string("").provideDelegate(this, "scriptName");
    private final StoredProperty<String> dsMappingScenario =
            string("").provideDelegate(this, "mappingScenario");

    private final StoredProperty<String> outputMimeType =
            string("").provideDelegate(this, "outputMimeType");
    public String getScriptName() {
        return dsScriptName.getValue(this);
    }

    public void setScriptName(String scriptName) {
        dsScriptName.setValue(this, scriptName);
    }

    public String getMappingScenario() {
        return dsMappingScenario.getValue(this);
    }

    public void setMappingScenario(String mappingScenario) {
        dsMappingScenario.setValue(this, mappingScenario);
    }

    public String getOutputMimeType() {
        return outputMimeType.getValue(this);
    }

    public void setOutputMimeType(String mimeType) {
        outputMimeType.setValue(this, mimeType);
    }

}