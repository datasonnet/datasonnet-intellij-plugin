package io.portx.datasonnet.editor;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * Created by eberman on 4/22/17.
 */
public class AutoSyncAction extends ToggleAction {
    DataSonnetEditor editor;

    final static Icon autosyncIcon = IconLoader.findIcon("/icons/autosync.png");

    public AutoSyncAction(DataSonnetEditor editor) {
        super("Auto Refresh", "Toggle Auto Refresh Mapping On/Off", autosyncIcon);
        this.editor = editor;
        setSelected(null, true);
    }

    @Override
    public boolean isSelected(AnActionEvent anActionEvent) {
        return editor.isAutoSync();
    }

    @Override
    public void setSelected(AnActionEvent anActionEvent, boolean b) {
        editor.setAutoSync(b);
    }
}
