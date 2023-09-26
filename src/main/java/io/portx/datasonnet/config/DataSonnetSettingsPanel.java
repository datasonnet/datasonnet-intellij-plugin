package io.portx.datasonnet.config;

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

public class DataSonnetSettingsPanel {
    private JPanel myWholePanel;
    private TextFieldWithBrowseButton myDataSonnetExecPathField;
    private JPanel myGlobalSettingsPanel;
    private JPanel myProjectSettingsPanel;
    private JBList myDataSonnetPathList;
    private JPanel mySearchPathPanel;
    private JRadioButton builtInParser;
    private JRadioButton externalParser;
    private JRadioButton extVars;
    private JRadioButton tlfArgs;

    private DataSonnetSettingsComponent mySettingsComponent;
    private DataSonnetProjectSettingsComponent myProjectSettingsComponent;

    private CollectionListModel<String> myDataSonnetPathsModel;

    public JComponent createPanel(@NotNull DataSonnetSettingsComponent settingsComponent, @NotNull DataSonnetProjectSettingsComponent projectSettingsComponent) {
        mySettingsComponent = settingsComponent;
        myProjectSettingsComponent = projectSettingsComponent;

        myGlobalSettingsPanel.setBorder(IdeBorderFactory.createTitledBorder("Application settings"));
        myProjectSettingsPanel.setBorder(IdeBorderFactory.createTitledBorder("Project settings"));

        FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true, false, false, false, false, false);

        myDataSonnetExecPathField.addBrowseFolderListener(
                "",
                "DataSonnet executable path",
                null,
                fileChooserDescriptor,
                TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT);

        //fileChooserDescriptor = new FileChooserDescriptor(true, false, false, false, false, false);

        myDataSonnetExecPathField.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                myDataSonnetExecPathField
                        .getTextField().setForeground(StringUtil.equals(myDataSonnetExecPathField.getText(), mySettingsComponent.getState().getDataSonnetExecPath()) ?
                        getDefaultValueColor() : getChangedValueColor());
            }
        });

        myDataSonnetPathsModel = new CollectionListModel<String>();
        myDataSonnetPathList = new JBList(myDataSonnetPathsModel);
        myDataSonnetPathList.getEmptyText().setText("No additional DataSonnet paths");
        myDataSonnetPathList.setCellRenderer(new ColoredListCellRenderer() {
            @Override
            protected void customizeCellRenderer(@NotNull JList list, Object value, int index, boolean selected, boolean hasFocus) {
                append(value.toString(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
            }
        });

        ToolbarDecorator toolbarDecorator = ToolbarDecorator.createDecorator(myDataSonnetPathList);

        toolbarDecorator.setAddAction(new AnActionButtonRunnable() {
            @Override
            public void run(AnActionButton anActionButton) {
                FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(false, true, false, false, false, false);
                final @NotNull VirtualFile dir = FileChooser.chooseFile(fileChooserDescriptor, null, null);
                myDataSonnetPathsModel.add(dir.getCanonicalPath());
            }
        });

        mySearchPathPanel.add(toolbarDecorator.createPanel(), BorderLayout.CENTER);

        AbstractAction enableComponentsAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myDataSonnetExecPathField.setEnabled(!builtInParser.isSelected());
                extVars.setEnabled(!builtInParser.isSelected());
                tlfArgs.setEnabled(!builtInParser.isSelected());
            }
        };

        builtInParser.addActionListener(enableComponentsAction);
        externalParser.addActionListener(enableComponentsAction);

        return myWholePanel;
    }

    public boolean isModified() {
        return !Comparing.equal(myDataSonnetExecPathField.getText(), mySettingsComponent.getState().getDataSonnetExecPath()) ||
               !Comparing.equal(myDataSonnetPathsModel.getItems(), myProjectSettingsComponent.getState().getDataSonnetLibraryPaths()) ||
               !Comparing.equal(extVars.isSelected(), mySettingsComponent.getState().isExtVars()) ||
               !Comparing.equal(builtInParser.isSelected(), mySettingsComponent.getState().isBuiltInParser());
    }

    public void apply() {
        mySettingsComponent.getState().setDataSonnetExecPath(myDataSonnetExecPathField.getText());
        mySettingsComponent.getState().setBuiltInParser(builtInParser.isSelected());
        mySettingsComponent.getState().setExtVars(extVars.isSelected());
        java.util.List<String> dataSonnetPaths = new ArrayList<String>();
        dataSonnetPaths.addAll(myDataSonnetPathsModel.getItems());
        myProjectSettingsComponent.getState().setDataSonnetLibraryPaths(dataSonnetPaths);
    }

    public void reset() {
        myDataSonnetExecPathField.setText(mySettingsComponent.getState().getDataSonnetExecPath());
        myDataSonnetPathsModel.replaceAll(myProjectSettingsComponent.getState().getDataSonnetLibraryPaths());
        boolean isBIP = mySettingsComponent.getState().isBuiltInParser();
        externalParser.setSelected(!isBIP);
        builtInParser.setSelected(isBIP);
        myDataSonnetExecPathField.setEnabled(!isBIP);
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
