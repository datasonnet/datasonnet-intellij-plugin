package io.portx.datasonnet.editor;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.AsyncFileEditorProvider;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorImpl;
import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorProvider;
import com.intellij.openapi.fileEditor.impl.text.TextEditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import io.portx.datasonnet.language.DataSonnetFileType;
import io.portx.datasonnet.language.psi.DataSonnetFile;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by eberman on 11/3/16.
 */
public class DataSonnetEditorProvider extends PsiAwareTextEditorProvider {

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
    public Object createEditorBuilder(@NotNull Project project, @NotNull VirtualFile file, @Nullable Document document, @NotNull Continuation<? super Builder> $completion) {
        return new Builder() {
            @NotNull
            @Override
            public FileEditor build() {
                return new DataSonnetEditor(project, file, (TextEditorImpl)DataSonnetEditorProvider.super.createEditor(project, file));
            }
        };
    }

    @NotNull
    @Override
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        return new DataSonnetEditor(project, virtualFile, (TextEditorImpl)super.createEditor(project, virtualFile));
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
