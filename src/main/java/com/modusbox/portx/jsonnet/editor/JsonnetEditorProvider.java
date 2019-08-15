package com.modusbox.portx.jsonnet.editor;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.modusbox.portx.jsonnet.language.JsonnetFileType;
import com.modusbox.portx.jsonnet.language.psi.JsonnetFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by eberman on 11/3/16.
 */
public class JsonnetEditorProvider extends TextEditorProvider { //implements FileEditorProvider {

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        boolean isJsonnetFile = false;
        PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
        if (psiFile != null)
            isJsonnetFile = (psiFile instanceof JsonnetFile);
        else {
            List<String> extensions = JsonnetFileType.INSTANCE.getExtensions();
            isJsonnetFile = (virtualFile.getExtension() != null && extensions.contains(virtualFile.getExtension().toLowerCase()));
        }
        return isJsonnetFile;
    }

    @NotNull
    @Override
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        return new JsonnetEditor(project, virtualFile, this);
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
