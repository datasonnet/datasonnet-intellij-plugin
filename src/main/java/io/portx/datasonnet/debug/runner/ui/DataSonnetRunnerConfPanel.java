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


import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.PanelWithAnchor;
import io.portx.datasonnet.editor.DataSonnetEditor;
import io.portx.datasonnet.engine.Scenario;
import io.portx.datasonnet.engine.ScenarioManager;
import io.portx.datasonnet.language.DataSonnetFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.List;

public class DataSonnetRunnerConfPanel implements PanelWithAnchor {

    protected LabeledComponent<TextFieldWithBrowseButton> dsScriptFile;
    protected JPanel panel;
    private JComboBox mappingScenarioSelector;
    private JComboBox outputMimeType;
    protected JComponent anchor;

    private Project project;

    public DataSonnetRunnerConfPanel(@NotNull Project project) {
        this.project = project;
        dsScriptFile.getComponent().addBrowseFolderListener(
                "Select DataSonnet Mapping File", "", project,
                new FileChooserDescriptor(true, false, false, false, false, false) {
                    @Override
                    public boolean isFileSelectable(@Nullable VirtualFile file) {
                        if (!super.isFileSelectable(file)) return false;
                        return file.getFileType() instanceof DataSonnetFileType;
                    }
                });

        dsScriptFile.getComponent().getTextField().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                loadMappingScenarios();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                mappingScenarioSelector.removeAllItems();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                loadMappingScenarios();
            }
        });

        outputMimeType.addItem("application/json");
        outputMimeType.addItem("application/xml");
        outputMimeType.addItem("application/csv");
        outputMimeType.addItem("text/plain");
    }

    public JComponent createComponent() {
        return panel;
    }

    public String getDisplayName() {
        return "DataSonnet Run Configuration";
    }

    @Override
    public JComponent getAnchor() {
        return anchor;
    }

    @Override
    public void setAnchor(JComponent anchor) {
        this.anchor = anchor;
        dsScriptFile.setAnchor(anchor);
    }

    protected void setDsScriptFile(String scriptFile) {
        dsScriptFile.getComponent().getTextField().setText(scriptFile);
        loadMappingScenarios();
    }
    protected void setMappingScenario(String mappingScenario) {
        if (mappingScenario != null) {
            mappingScenarioSelector.setSelectedItem(mappingScenario);
        }
    }
    protected void setOutputMimeType(String mimeType) {
        if (mimeType != null) {
            outputMimeType.setSelectedItem(mimeType);
        }
    }

    protected String getDsScriptFile() {
        return dsScriptFile.getComponent().getText();
    }

    protected String getMappingScenario() {
        return mappingScenarioSelector.getSelectedItem() != null ? mappingScenarioSelector.getSelectedItem().toString() : null;
    }

    protected String getOutputMimeType() {
        return outputMimeType.getSelectedItem() != null ? outputMimeType.getSelectedItem().toString() : null;
    }

    protected final void loadMappingScenarios() {
        mappingScenarioSelector.removeAllItems();

        String mappingAbsolutePath = dsScriptFile.getComponent().getText();
        VirtualFile dsVirtualFile = LocalFileSystem.getInstance().findFileByPath(mappingAbsolutePath);
        if (dsVirtualFile == null) {
            mappingScenarioSelector.setEnabled(false);
            return;
        }

        PsiFile dsPsiFile = PsiManager.getInstance(project).findFile(dsVirtualFile);
        if (dsPsiFile == null) {
            mappingScenarioSelector.setEnabled(false);
            return;
        }

        final ScenarioManager manager = ScenarioManager.getInstance(project);
        List<Scenario> scenarios = manager.getScenariosFor(dsPsiFile);

        mappingScenarioSelector.setEnabled(true);
        scenarios.forEach((scenario) -> mappingScenarioSelector.addItem(scenario.getPresentableText()));
    }
}
