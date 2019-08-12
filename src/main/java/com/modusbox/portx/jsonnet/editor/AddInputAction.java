package com.modusbox.portx.jsonnet.editor;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * Created by eberman on 4/22/17.
 */
public class AddInputAction extends AnAction {
    JsonnetEditor editor;

    public AddInputAction(JsonnetEditor editor)
    {
        super("Add new input", "Adds a new input to the scenario", AllIcons.General.Add);
        this.editor = editor;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        String jsonnetName = this.editor.getPsiFile().getVirtualFile().getCanonicalPath();
        Scenario currentScenario = ScenarioManager.getInstance(e.getProject()).getCurrentScenario(jsonnetName);
        AddInputDialog dialog = new AddInputDialog(e.getProject(), currentScenario, this.editor.getPsiFile());
        dialog.show();
        editor.loadScenario(currentScenario);
    }
}
