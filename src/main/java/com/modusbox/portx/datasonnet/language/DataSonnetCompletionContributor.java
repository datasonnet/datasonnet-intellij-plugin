package com.modusbox.portx.datasonnet.language;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.util.ProcessingContext;
import com.modusbox.portx.datasonnet.language.psi.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.modusbox.portx.datasonnet.language.DataSonnetIdentifierReference.findIdentifierFromParams;
import static com.modusbox.portx.datasonnet.language.DataSonnetIdentifierReference.isFunctionExpr;

public class DataSonnetCompletionContributor extends CompletionContributor {
    public DataSonnetCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(DataSonnetTypes.IDENTIFIER).withLanguage(DataSonnetLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        resultSet.addElement(LookupElementBuilder.create("null"));
                        resultSet.addElement(LookupElementBuilder.create("true"));
                        resultSet.addElement(LookupElementBuilder.create("false"));
                        resultSet.addElement(LookupElementBuilder.create("local"));
                        PsiElement element = parameters.getPosition().getOriginalElement();

                        while (element != null) {
                            if (element instanceof DataSonnetOuterlocal) {
                                List<DataSonnetBind> binds = DataSonnetIdentifierReference.findBindInOuterLocal((DataSonnetOuterlocal) element);
                                for (DataSonnetBind b: binds) {
                                    resultSet.addElement(LookupElementBuilder.create(b.getIdentifier0().getText()));
                                }
                            } else if (element instanceof DataSonnetExpr0 && isFunctionExpr((DataSonnetExpr0) element)) {
                                List<DataSonnetIdentifier0> identifiers = DataSonnetIdentifierReference.findIdentifierFromFunctionExpr0((DataSonnetExpr0) element);
                                for (DataSonnetIdentifier0 i: identifiers) {
                                    resultSet.addElement(LookupElementBuilder.create(i.getText()));
                                }
                            }else if (element instanceof DataSonnetObjinside) {
                                List<DataSonnetObjlocal> locals = ((DataSonnetObjinside)element).getObjlocalList();
                                for (DataSonnetMember m: ((DataSonnetObjinside)element).getMemberList()){
                                    if (m.getObjlocal() != null){
                                        locals.add(m.getObjlocal());
                                    }
                                }
                                for (DataSonnetObjlocal local: locals) {
                                    DataSonnetBind b = local.getBind();
                                    resultSet.addElement(LookupElementBuilder.create(b.getIdentifier0().getText()));
                                }
                            }else if (element.getParent() instanceof DataSonnetBind &&
                                    ((DataSonnetBind)element.getParent()).getExpr() == element){
                                List<DataSonnetIdentifier0> idents = findIdentifierFromParams(((DataSonnetBind)element.getParent()).getParams());
                                for(DataSonnetIdentifier0 ident: idents){
                                    resultSet.addElement(LookupElementBuilder.create(ident.getText()));
                                }
                            }else if (element.getParent() instanceof DataSonnetField &&
                                    ((DataSonnetField)element.getParent()).getExpr() == element){
                                List<DataSonnetIdentifier0> idents = findIdentifierFromParams(((DataSonnetField)element.getParent()).getParams());
                                for(DataSonnetIdentifier0 ident: idents){
                                    resultSet.addElement(LookupElementBuilder.create(ident.getText()));
                                }
                            }
                            element = element.getParent();
                        }
                    }
                }
        );
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(DataSonnetTypes.DOUBLE_QUOTED_STRING).withLanguage(DataSonnetLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        if (checkIfImport(parameters.getPosition())) {
                            String text = parameters.getPosition().getText();
                            addFileCompletions(parameters.getOriginalFile(), text, resultSet);
                        }
                    }
                }
        );
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(DataSonnetTypes.DOT).withLanguage(DataSonnetLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        resultSet.addElement(LookupElementBuilder.create("ZHOOOOPAAAA"));
                    }
                }
        );

    }

    private static boolean checkIfImport(PsiElement position) {
        return position.getPrevSibling() != null &&
          position.getPrevSibling().getPrevSibling().getNode().getElementType().equals(DataSonnetTypes.IMPORT);
    }

    private static void addFileCompletions(PsiFile file, String current, CompletionResultSet set) {
        // current always begins with a "
        String cleanedCurrent = current.substring(1);
        if (cleanedCurrent.endsWith("\"")) {
           cleanedCurrent = cleanedCurrent.substring(0, cleanedCurrent.length() - 1);
        }

        if (!cleanedCurrent.endsWith(Constants.INTELLIJ_RULES)) {
            return;
        }
        Path currentPath = Paths.get(file.getContainingDirectory().getVirtualFile().getPath());
        String stripped = cleanedCurrent.replace(Constants.INTELLIJ_RULES, "");
        Path strippedPath = Paths.get(stripped);
        int strippedPathCount = strippedPath.getNameCount();

        File prefixFile;
        String input;
        if (stripped.endsWith("/")) {
            prefixFile = currentPath.resolve(Paths.get(stripped)).toFile();
            input = "";
        } else if (strippedPathCount == 1){
            prefixFile = currentPath.toFile();
            input = stripped;
        } else {
            prefixFile = currentPath.resolve(strippedPath.subpath(0, strippedPathCount - 1)).toFile();
            input = strippedPath.subpath(strippedPathCount-1, strippedPathCount).toString();
        }

        CompletionResultSet replaceSet = set.withPrefixMatcher(stripped);
        if (prefixFile.isDirectory()) {
            File[] files = prefixFile.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.startsWith(input);
                }
            });
            for (File f: files) {
               String result = stripped + f.getName().substring(input.length());
               replaceSet.addElement(LookupElementBuilder.create(result));
            }
        }
    }
}
