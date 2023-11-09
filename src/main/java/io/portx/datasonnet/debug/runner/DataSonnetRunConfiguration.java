/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.portx.datasonnet.debug.runner;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import io.portx.datasonnet.debug.runner.ui.DataSonnetSettingsEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DataSonnetRunConfiguration extends RunConfigurationBase<DataSonnetRunConfigurationOptions> implements RunConfigurationWithSuppressedDefaultRunAction {

    protected DataSonnetRunConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    @NotNull
    @Override
    protected DataSonnetRunConfigurationOptions getOptions() {
        return (DataSonnetRunConfigurationOptions) super.getOptions();
    }

    public String getScriptName() {
        return getOptions().getScriptName();
    }

    public void setScriptName(String scriptName) {
        getOptions().setScriptName(scriptName);
    }

    public String getMappingScenario() {
        return getOptions().getMappingScenario();
    }

    public void setMappingScenario(String mappingScenario) {
        getOptions().setMappingScenario(mappingScenario);
    }

    public String getOutputMimeType() {
        return getOptions().getOutputMimeType();
    }

    public void setOutputMimeType(String outputMimeType) {
        getOptions().setOutputMimeType(outputMimeType);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new DataSonnetSettingsEditor(this);
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        super.checkConfiguration();

        if (getScriptName() == null || "".equals(getScriptName().trim()))
            throw new RuntimeConfigurationException("DataSonnet mapping file must be selected");
        if (getMappingScenario() == null || "".equals(getMappingScenario().trim()))
            throw new RuntimeConfigurationException("Create new or select existing mapping scenario");
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor,
                                    @NotNull ExecutionEnvironment environment) {
        return new DataSonnetRunProfileState(this);
    }
}
