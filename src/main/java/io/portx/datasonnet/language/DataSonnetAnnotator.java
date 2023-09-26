package io.portx.datasonnet.language;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import io.portx.datasonnet.language.psi.DataSonnetFieldname;
import io.portx.datasonnet.language.psi.DataSonnetIdentifier0;
import io.portx.datasonnet.language.psi.DataSonnetSelect;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class DataSonnetAnnotator implements Annotator {
    TextAttributesKey MEMBER = createTextAttributesKey("DataSonnet_MEMBER", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof DataSonnetIdentifier0 && psiElement.getParent() instanceof DataSonnetFieldname){
            annotationHolder.newAnnotation(HighlightSeverity.WEAK_WARNING, "").textAttributes(MEMBER).range(psiElement).create();
        }
        if (psiElement instanceof DataSonnetIdentifier0 && psiElement.getParent() instanceof DataSonnetSelect){
            annotationHolder.newAnnotation(HighlightSeverity.WEAK_WARNING, "").textAttributes(MEMBER).range(psiElement).create();
        }
    }
}