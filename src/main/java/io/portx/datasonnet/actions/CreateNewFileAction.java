package io.portx.datasonnet.actions;

import com.intellij.ide.actions.CreateFileAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import io.portx.datasonnet.config.DataSonnetProjectSettings;
import io.portx.datasonnet.config.DataSonnetProjectSettingsComponent;
import io.portx.datasonnet.language.DataSonnetIcon;
import org.jetbrains.annotations.NotNull;

public class CreateNewFileAction extends CreateFileAction implements DumbAware {

    public CreateNewFileAction() {
        super(() -> "DataSonnet File", () -> "Create New DataSonnet File.", () -> DataSonnetIcon.FILE);
    }

    @Override
    protected String getDefaultExtension() {
        return "ds";
    }

    @Override
    protected PsiElement @NotNull [] create(@NotNull String newName, @NotNull PsiDirectory directory) throws Exception {
        // Call the super method to create the new file.
        final PsiElement[] elements = super.create(newName, directory);

        final Project project = directory.getProject();
        final DataSonnetProjectSettings settings = DataSonnetProjectSettingsComponent.getSettings(project);
        if (elements.length == 1) {
            final PsiFile file = (PsiFile) elements[0];
            final VirtualFile virtualFile = file.getVirtualFile();
            if (virtualFile != null) {
                final Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
                if (document != null) {
                    WriteCommandAction.runWriteCommandAction(project, () -> document.setText
                            (StringUtil.convertLineSeparators(settings.getDefaultTemplate())));
                }
            }
        }

        return elements;    }
}

