package com.modusbox.portx.jsonnet.config;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.*;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class JsonnetSettingsPanel {
    private JPanel myWholePanel;
    private TextFieldWithBrowseButton myJsonnetExecPathField;
    private JPanel myGlobalSettingsPanel;
    private JPanel myProjectSettingsPanel;
    private JBList myJsonnetPathList;
    private JPanel mySearchPathPanel;
    private JRadioButton builtInParser;
    private JRadioButton externalParser;
    private JRadioButton extVars;
    private JRadioButton tlfArgs;

    private JsonnetSettingsComponent mySettingsComponent;
    private JsonnetProjectSettingsComponent myProjectSettingsComponent;

    private CollectionListModel<String> myJsonnetPathsModel;

    public JComponent createPanel(@NotNull JsonnetSettingsComponent settingsComponent, @NotNull JsonnetProjectSettingsComponent projectSettingsComponent) {
        mySettingsComponent = settingsComponent;
        myProjectSettingsComponent = projectSettingsComponent;

        myGlobalSettingsPanel.setBorder(IdeBorderFactory.createTitledBorder("Application settings"));
        myProjectSettingsPanel.setBorder(IdeBorderFactory.createTitledBorder("Project settings"));

        FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true, false, false, false, false, false);

        myJsonnetExecPathField.addBrowseFolderListener(
                "",
                "Jsonnet executable path",
                null,
                fileChooserDescriptor,
                TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT);

        //fileChooserDescriptor = new FileChooserDescriptor(true, false, false, false, false, false);

        myJsonnetExecPathField.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                myJsonnetExecPathField
                        .getTextField().setForeground(StringUtil.equals(myJsonnetExecPathField.getText(), mySettingsComponent.getState().getJsonnetExecPath()) ?
                        getDefaultValueColor() : getChangedValueColor());
            }
        });

        myJsonnetPathsModel = new CollectionListModel<String>();
        myJsonnetPathList = new JBList(myJsonnetPathsModel);
        myJsonnetPathList.getEmptyText().setText("No additional Jsonnet paths");
        myJsonnetPathList.setCellRenderer(new ColoredListCellRenderer() {
            @Override
            protected void customizeCellRenderer(@NotNull JList list, Object value, int index, boolean selected, boolean hasFocus) {
                append(value.toString(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
            }
        });

        ToolbarDecorator toolbarDecorator = ToolbarDecorator.createDecorator(myJsonnetPathList);

        toolbarDecorator.setAddAction(new AnActionButtonRunnable() {
            @Override
            public void run(AnActionButton anActionButton) {
                FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(false, true, false, false, false, false);
                final @NotNull VirtualFile dir = FileChooser.chooseFile(fileChooserDescriptor, null, null);
                myJsonnetPathsModel.add(dir.getCanonicalPath());
            }
        });

        mySearchPathPanel.add(toolbarDecorator.createPanel(), BorderLayout.CENTER);

        AbstractAction enableComponentsAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myJsonnetExecPathField.setEnabled(!builtInParser.isSelected());
                extVars.setEnabled(!builtInParser.isSelected());
                tlfArgs.setEnabled(!builtInParser.isSelected());
            }
        };

        builtInParser.addActionListener(enableComponentsAction);
        externalParser.addActionListener(enableComponentsAction);

        return myWholePanel;
    }

    public boolean isModified() {
        return !Comparing.equal(myJsonnetExecPathField.getText(), mySettingsComponent.getState().getJsonnetExecPath()) ||
               !Comparing.equal(myJsonnetPathsModel.getItems(), myProjectSettingsComponent.getState().getJsonnetLibraryPaths()) ||
               !Comparing.equal(extVars.isSelected(), mySettingsComponent.getState().isExtVars()) ||
               !Comparing.equal(builtInParser.isSelected(), mySettingsComponent.getState().isBuiltInParser());
    }

    public void apply() {
        mySettingsComponent.getState().setJsonnetExecPath(myJsonnetExecPathField.getText());
        mySettingsComponent.getState().setBuiltInParser(builtInParser.isSelected());
        mySettingsComponent.getState().setExtVars(extVars.isSelected());
        java.util.List<String> jsonnetPaths = new ArrayList<String>();
        jsonnetPaths.addAll(myJsonnetPathsModel.getItems());
        myProjectSettingsComponent.getState().setJsonnetLibraryPaths(jsonnetPaths);
    }

    public void reset() {
        myJsonnetExecPathField.setText(mySettingsComponent.getState().getJsonnetExecPath());
        myJsonnetPathsModel.replaceAll(myProjectSettingsComponent.getState().getJsonnetLibraryPaths());
        boolean isBIP = mySettingsComponent.getState().isBuiltInParser();
        externalParser.setSelected(!isBIP);
        builtInParser.setSelected(isBIP);
        myJsonnetExecPathField.setEnabled(!isBIP);
        extVars.setEnabled(!isBIP);
        tlfArgs.setEnabled(!isBIP);
    }

    public Color getDefaultValueColor() {
        return findColorByKey("TextField.inactiveForeground", "nimbusDisabledText");
    }

    @NotNull
    private static Color findColorByKey(String... colorKeys) {
        Color c = null;
        for (String key : colorKeys) {
            c = UIManager.getColor(key);
            if (c != null) {
                break;
            }
        }

        assert c != null : "Can't find color for keys " + Arrays.toString(colorKeys);
        return c;
    }

    public Color getChangedValueColor() {
        return findColorByKey("TextField.foreground");
    }
}
