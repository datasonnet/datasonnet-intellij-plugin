package io.portx.datasonnet.editor;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import io.portx.datasonnet.language.DataSonnetFileType;
import io.portx.datasonnet.language.psi.DataSonnetFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by eberman on 11/3/16.
 */
public class DataSonnetEditorProvider extends PsiAwareTextEditorProvider { //implements FileEditorProvider {

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        boolean isDataSonnetFile = false;
        PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
        if (psiFile != null)
            isDataSonnetFile = (psiFile instanceof DataSonnetFile);
        else {
            List<String> extensions = DataSonnetFileType.INSTANCE.getExtensions();
            isDataSonnetFile = (virtualFile.getExtension() != null && extensions.contains(virtualFile.getExtension().toLowerCase()));
        }
        return isDataSonnetFile;
    }

    @NotNull
    @Override
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        return new DataSonnetEditor(project, virtualFile, this);
    }

    @NotNull
    @Override
    public String getEditorTypeId() {
        return "Datasonnet-Editor";
    }

    @NotNull
    @Override
    public FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_BEFORE_DEFAULT_EDITOR;
    }

}
