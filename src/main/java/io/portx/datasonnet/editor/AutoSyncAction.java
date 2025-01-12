package io.portx.datasonnet.editor;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.util.IconLoader;
import io.portx.datasonnet.config.DataSonnetProjectSettings;
import io.portx.datasonnet.config.DataSonnetProjectSettingsComponent;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by eberman on 4/22/17.
 */
public class AutoSyncAction extends ToggleAction {
    DataSonnetEditor editor;

    final static Icon autosyncIcon = IconLoader.findIcon("/icons/autosync.png", AutoSyncAction.class);

    public AutoSyncAction(DataSonnetEditor editor) {
        super("Auto Refresh", "Toggle Auto Refresh Mapping On/Off", autosyncIcon);
        this.editor = editor;

        final DataSonnetProjectSettings settings = DataSonnetProjectSettingsComponent.getSettings(editor.getProject());
        final boolean autoRefresh = settings.getAutoRefresh();
        setSelected(null, autoRefresh);
    }

    @Override
    public boolean isSelected(AnActionEvent anActionEvent) {
        return editor.isAutoSync();
    }

    @Override
    public void setSelected(AnActionEvent anActionEvent, boolean b) {
        editor.setAutoSync(b);

        // Save the setting
        final DataSonnetProjectSettings settings = DataSonnetProjectSettingsComponent.getSettings(editor.getProject());
        settings.setAutoRefresh(b);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
