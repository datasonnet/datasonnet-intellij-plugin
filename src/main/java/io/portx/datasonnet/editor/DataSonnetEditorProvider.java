package io.portx.datasonnet.editor;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.fileEditor.impl.text.LargeFileEditorProvider;
import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorProvider;
import com.intellij.openapi.fileEditor.impl.text.TextEditorImpl;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import io.portx.datasonnet.language.DataSonnetFileType;
import io.portx.datasonnet.language.psi.DataSonnetFile;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.GlobalScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by eberman on 11/3/16.
 */
public class DataSonnetEditorProvider extends PsiAwareTextEditorProvider {
    DataSonnetEditor editor = null;
    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        boolean isDataSonnetFile = false;
        PsiFile psiFile = ApplicationManager.getApplication().runReadAction((Computable<PsiFile>) () -> PsiManager.getInstance(project).findFile(virtualFile));
        if (psiFile != null)
            isDataSonnetFile = (psiFile instanceof DataSonnetFile);
        else {
            List<String> extensions = DataSonnetFileType.INSTANCE.getExtensions();
            isDataSonnetFile = (virtualFile.getExtension() != null && extensions.contains(virtualFile.getExtension().toLowerCase()));
        }
        return isDataSonnetFile;
    }

    @Nullable
    @Override
    public Object createFileEditor(Project project, VirtualFile virtualFile, Document document, CoroutineScope scope, Continuation completion) {
        ApplicationManager.getApplication().invokeAndWait(new Runnable() {
            @Override
            public void run() {
                editor = new DataSonnetEditor(project, virtualFile, (TextEditorImpl) DataSonnetEditorProvider.super.createEditor(project, virtualFile));
            }
        });
        return editor;
    }


    @NotNull
    @Override
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        return new DataSonnetEditor(project, virtualFile, (TextEditorImpl) super.createEditor(project, virtualFile));
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
