package io.portx.datasonnet.actions;

import com.intellij.ide.actions.CreateFileAction;
import com.intellij.openapi.project.DumbAware;
import io.portx.datasonnet.language.DataSonnetIcon;

public class CreateNewFileAction extends CreateFileAction implements DumbAware {

    public CreateNewFileAction() {
        super("DataSonnet File", "Create New DataSonnet File.", DataSonnetIcon.FILE);
    }

    @Override
    protected String getDefaultExtension() {
        return "ds";
    }
}

