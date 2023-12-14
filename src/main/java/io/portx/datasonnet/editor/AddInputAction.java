package io.portx.datasonnet.editor;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.util.IconLoader;
import io.portx.datasonnet.engine.Scenario;
import io.portx.datasonnet.engine.ScenarioManager;

/**
 * Created by eberman on 4/22/17.
 */
public class AddInputAction extends AnAction {
    DataSonnetEditor editor;

    public AddInputAction(DataSonnetEditor editor) {
        super("Add new input", "Adds a new input to the scenario", IconLoader.findIcon("/icons/addInput.svg", AddInputAction.class));
        this.editor = editor;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        String dataSonnetName = this.editor.getPsiFile().getVirtualFile().getCanonicalPath();
        Scenario currentScenario = ScenarioManager.getInstance(e.getProject()).getCurrentScenario(dataSonnetName);
        AddInputDialog dialog = new AddInputDialog(e.getProject(), currentScenario, this.editor.getPsiFile());
        dialog.show();
        editor.loadScenario(currentScenario);
    }
}
