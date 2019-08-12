package com.modusbox.portx.jsonnet.editor;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.impl.text.TextEditorImpl;
import com.intellij.ui.JBSplitter;

import javax.swing.*;
import java.awt.*;

/**
 * Created by eberman on 11/3/16.
 */
public class JsonnetEditorUI {

    private JPanel rootPanel;

    private JPanel editorPanel;
    private JPanel outputPanel;
    private JPanel sourcePanel;
    private JPanel jsonnetPanel;

    private JBSplitter inputsSplitter;

    final static Logger logger = Logger.getInstance(JsonnetEditorUI.class);

    public JsonnetEditorUI(TextEditorImpl textEditor) {
        editorPanel.add(textEditor.getComponent(), BorderLayout.CENTER);

        JBSplitter splitter = new JBSplitter(false);
        splitter.setFirstComponent(editorPanel);
        splitter.setSecondComponent(outputPanel);
        splitter.setDividerWidth(3);
        splitter.setShowDividerControls(true);
        splitter.setProportion(0.5f);
        splitter.setAndLoadSplitterProportionKey("jsonnet.splitter.key");
        jsonnetPanel.add(splitter);

        sourcePanel.setSize(0, 0);
        inputsSplitter = new JBSplitter(false);
        inputsSplitter.setFirstComponent(sourcePanel);
        inputsSplitter.setSecondComponent(jsonnetPanel);
        inputsSplitter.setDividerWidth(3);
        inputsSplitter.setShowDividerControls(true);
        inputsSplitter.setProportion(0.0f);
        inputsSplitter.setAndLoadSplitterProportionKey("jsonnet.splitter2.key");
        //splitter2.setHonorComponentsMinimumSize(true);

        rootPanel.add(inputsSplitter);
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public void setRootPanel(JPanel rootPanel) {
        this.rootPanel = rootPanel;
    }

    public JPanel getEditorPanel() {
        return editorPanel;
    }

    public void setEditorPanel(JPanel editorPanel) {
        this.editorPanel = editorPanel;
    }

    public JPanel getOutputPanel() {
        return outputPanel;
    }

    public void setOutputPanel(JPanel outputPanel) {
        this.outputPanel = outputPanel;
    }

    public JPanel getSourcePanel() {
        return sourcePanel;
    }

    public void setSourcePanel(JPanel sourcePanel) {
        this.sourcePanel = sourcePanel;
    }

    public JPanel getJsonnetPanel() {
        return jsonnetPanel;
    }

    public void setJsonnetPanel(JPanel jsonnetPanel) {
        this.jsonnetPanel = jsonnetPanel;
    }

    public JBSplitter getInputsSplitter() {
        return inputsSplitter;
    }

    public void setInputsSplitter(JBSplitter inputsSplitter) {
        this.inputsSplitter = inputsSplitter;
    }
}
