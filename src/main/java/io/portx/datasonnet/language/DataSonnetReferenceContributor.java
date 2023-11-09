package io.portx.datasonnet.language;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import io.portx.datasonnet.language.psi.DataSonnetIdentifier0;
import io.portx.datasonnet.language.psi.DataSonnetImportop;
import io.portx.datasonnet.language.psi.DataSonnetImportstrop;
import org.jetbrains.annotations.NotNull;

public class DataSonnetReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement(DataSonnetImportop.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element,
                                                                 @NotNull ProcessingContext
                                                                         context) {
                        return new PsiReference[]{
                                new DataSonnetImportopReference(
                                        element,
                                        element.getTextRange().shiftRight(-element.getTextOffset())
                                )
                        };
                    }
                }
        );
        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement(DataSonnetImportstrop.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element,
                                                                 @NotNull ProcessingContext
                                                                         context) {
                        return new PsiReference[]{
                                new DataSonnetImportopReference(
                                        element,
                                        element.getTextRange().shiftRight(-element.getTextOffset())
                                )
                        };
                    }
                }
        );
        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement(DataSonnetIdentifier0.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element,
                                                                 @NotNull ProcessingContext
                                                                         context) {
                        return new PsiReference[]{
                                new DataSonnetIdentifierReference(
                                        element,
                                        element.getTextRange().shiftRight(-element.getTextOffset())
                                )
                        };
                    }
                }
        );
    }
}