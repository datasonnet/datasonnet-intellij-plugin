package io.portx.datasonnet.config;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.*;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class DataSonnetSettingsPanel {
    private JPanel myWholePanel;
    private JPanel myProjectSettingsPanel;
    private JBList myDataSonnetPathList;
    private JPanel mySearchPathPanel;
    private JPanel myTemplatePanel;
    private JTextArea myTemplateTextArea;

    private DataSonnetProjectSettingsComponent myProjectSettingsComponent;

    private CollectionListModel<String> myDataSonnetPathsModel;

    public JComponent createPanel(@NotNull DataSonnetProjectSettingsComponent projectSettingsComponent) {
        myProjectSettingsComponent = projectSettingsComponent;
        myProjectSettingsPanel.setBorder(IdeBorderFactory.createTitledBorder("Project settings"));

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
                if (dir != null) {
                    myDataSonnetPathsModel.add(dir.getCanonicalPath());
                }
            }
        });

        mySearchPathPanel.add(toolbarDecorator.createPanel(), BorderLayout.CENTER);

        return myWholePanel;
    }

    public boolean isModified() {
        final DataSonnetProjectSettings settings = myProjectSettingsComponent.getState();
        return  !Objects.equals(myTemplateTextArea.getText(), settings.getDefaultTemplate()) ||
                !Objects.equals(myDataSonnetPathsModel.getItems(), settings.getDataSonnetLibraryPaths());
    }

    public void apply() {
        final DataSonnetProjectSettings settings = myProjectSettingsComponent.getState();

        // Apply the default template for a new DataSonnet file
        settings.setDefaultTemplate(myTemplateTextArea.getText());

        // Apply the DataSonnet library paths
        java.util.List<String> dataSonnetPaths = new ArrayList<String>();
        dataSonnetPaths.addAll(myDataSonnetPathsModel.getItems());
        settings.setDataSonnetLibraryPaths(dataSonnetPaths);
    }

    public void reset() {
        final DataSonnetProjectSettings settings = myProjectSettingsComponent.getState();
        myTemplateTextArea.setText(settings.getDefaultTemplate());
        myDataSonnetPathsModel.replaceAll(settings.getDataSonnetLibraryPaths());
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
