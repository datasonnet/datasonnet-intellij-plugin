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
package io.portx.datasonnet.debug.runner.ui;

import com.intellij.openapi.options.SettingsEditor;
import io.portx.datasonnet.debug.runner.DataSonnetRunConfiguration;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class DataSonnetSettingsEditor extends SettingsEditor<DataSonnetRunConfiguration> {
    private final DataSonnetRunnerConfPanel configurationPanel;

    public DataSonnetSettingsEditor(DataSonnetRunConfiguration runnerConfiguration) {
        this.configurationPanel = new DataSonnetRunnerConfPanel(runnerConfiguration.getProject());
        super.resetFrom(runnerConfiguration);
    }

    @Override
    protected void resetEditorFrom(@NotNull DataSonnetRunConfiguration runnerConfiguration) {
        configurationPanel.setDsScriptFile(runnerConfiguration.getScriptName());
        configurationPanel.setMappingScenario(runnerConfiguration.getMappingScenario());
        configurationPanel.setOutputMimeType(runnerConfiguration.getOutputMimeType());
    }

    @Override
    protected void applyEditorTo(@NotNull DataSonnetRunConfiguration runnerConfiguration) {
        runnerConfiguration.setScriptName(configurationPanel.getDsScriptFile());
        runnerConfiguration.setMappingScenario(configurationPanel.getMappingScenario());
        runnerConfiguration.setOutputMimeType(configurationPanel.getOutputMimeType());
    }

    @Override
    protected @NotNull JComponent createEditor() {
        return this.configurationPanel.createComponent();
    }
}
