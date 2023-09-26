package io.portx.datasonnet.language;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import io.portx.datasonnet.language.psi.*;
import io.portx.datasonnet.language.psi.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DataSonnetCompletionContributor extends CompletionContributor {
    public DataSonnetCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(DataSonnetTypes.IDENTIFIER).withLanguage(DataSonnetLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               @NotNull ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        PsiElement element = parameters.getPosition().getOriginalElement();

                        while (element != null) {
                            if (element instanceof DataSonnetSelect) {
                                DataSonnetObj resolved = resolveExprToObj((DataSonnetExpr) element.getParent());
                                if (resolved != null) {
                                    addMembersFromObject(resolved, resultSet);
                                }
                                // Do not show suggestions from outer space if the element
                                // before the dot can be resolved. We are only interested in the fields.
                                return;
                            } else if (element instanceof DataSonnetOuterlocal) {
                                List<DataSonnetBind> binds = DataSonnetIdentifierReference.findBindInOuterLocal((DataSonnetOuterlocal) element);
                                for (DataSonnetBind b: binds) {
                                    resultSet.addElement(LookupElementBuilder.create(b.getIdentifier0().getText()));
                                }
                            } else if (element instanceof DataSonnetExpr0 && DataSonnetIdentifierReference.isFunctionExpr((DataSonnetExpr0) element)) {
                                List<DataSonnetIdentifier0> identifiers = DataSonnetIdentifierReference.findIdentifierFromFunctionExpr0((DataSonnetExpr0) element);
                                for (DataSonnetIdentifier0 i: identifiers) {
                                    resultSet.addElement(LookupElementBuilder.create(i.getText()));
                                }
                            } else if (element instanceof DataSonnetObjinside) {
                                List<DataSonnetObjlocal> locals = ((DataSonnetObjinside) element).getObjlocalList();
                                DataSonnetMembers members = ((DataSonnetObjinside) element).getMembers();
                                if (members != null) {
                                    for (DataSonnetMember m: members.getMemberList()){
                                        if (m.getObjlocal() != null){
                                            locals.add(m.getObjlocal());
                                        }
                                    }
                                }
                                for (DataSonnetObjlocal local: locals) {
                                    DataSonnetBind b = local.getBind();
                                    resultSet.addElement(LookupElementBuilder.create(b.getIdentifier0().getText()));
                                }
                            } else if (element.getParent() instanceof DataSonnetBind &&
                                    ((DataSonnetBind)element.getParent()).getExpr() == element){
                                List<DataSonnetIdentifier0> idents = DataSonnetIdentifierReference.findIdentifierFromParams(((DataSonnetBind)element.getParent()).getParams());
                                for(DataSonnetIdentifier0 ident: idents){
                                    resultSet.addElement(LookupElementBuilder.create(ident.getText()));
                                }
                            } else if (element.getParent() instanceof DataSonnetField &&
                                    ((DataSonnetField)element.getParent()).getExpr() == element){
                                List<DataSonnetIdentifier0> idents = DataSonnetIdentifierReference.findIdentifierFromParams(((DataSonnetField)element.getParent()).getParams());
                                for(DataSonnetIdentifier0 ident: idents){
                                    resultSet.addElement(LookupElementBuilder.create(ident.getText()));
                                }
                            }
                            element = element.getParent();
                        }

                        resultSet.addElement(LookupElementBuilder.create("null"));
                        resultSet.addElement(LookupElementBuilder.create("true"));
                        resultSet.addElement(LookupElementBuilder.create("false"));
                        resultSet.addElement(LookupElementBuilder.create("local"));
                    }
                }
        );
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(DataSonnetTypes.DOUBLE_QUOTED_STRING).withLanguage(DataSonnetLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               @NotNull ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        if (checkIfImport(parameters.getPosition())) {
                            String text = parameters.getPosition().getText();
                            addFileCompletions(parameters.getOriginalFile(), text, resultSet);
                        }
                    }
                }
        );
    }

    private static void addMembersFromObject(DataSonnetObj obj, CompletionResultSet resultSet) {
        if (obj.getObjinside() == null || obj.getObjinside().getMembers() == null) return;

        List<DataSonnetMember> memberList = obj.getObjinside().getMembers().getMemberList();
        for (DataSonnetMember member : memberList) {
//            if (member.getField() != null && member.getField().getFieldname().getIdentifier0() != null) {
//                String fieldName = member.getField().getFieldname().getIdentifier0().getText();
//                resultSet.addElement(LookupElementBuilder.create(fieldName));
//            }

            if (member.getField() != null && member.getField().getFieldname() != null) {
                String fieldName = member.getField().getFieldname().getText();
                if (fieldName.startsWith("\"") || fieldName.startsWith("\'")) {
                    fieldName = fieldName.substring(1);
                }
                if (fieldName.endsWith("\"") || fieldName.endsWith("\'")) {
                    fieldName = fieldName.substring(0, fieldName.length() - 1);
                }
                resultSet.addElement(LookupElementBuilder.create(fieldName));
            }

        }
    }

    private static DataSonnetObj resolveExprToObj(DataSonnetExpr expr) {
        return resolveExprToObj(expr, new ArrayList<>());
    }

    /**
     * Resolves an expression of the form x.y.z.(dummy token) to an instance of DataSonnetObj
     * if possible, otherwise returns null.
     * To avoid infinite loops, we keep track of the list of expressions visited along this
     * call chain.git p
     */
    private static DataSonnetObj resolveExprToObj(DataSonnetExpr expr, List<DataSonnetExpr> visited) {
        if (visited.contains(expr)) return null; // In the future we can give a warning here
        visited.add(expr);

        try {

            List<DataSonnetIdentifier0> selectList = new ArrayList<>();
            for (DataSonnetSelect select : expr.getSelectList()) {
                if (!select.getIdentifier0().getText().endsWith(Constants.INTELLIJ_RULES.trim())) {
                    selectList.add(select.getIdentifier0());
                }else{
                    break;
                }
            }
            return resolveExprToObj(expr, visited, selectList);
        }finally{
            visited.remove(expr);
        }
    }
    static DataSonnetObj resolveExprToObj(DataSonnetExpr expr, List<DataSonnetExpr> visited, List<DataSonnetIdentifier0> selectList) {
        DataSonnetExpr0 first = expr.getExpr0();
        DataSonnetObj curr = resolveExpr0ToObj(first, visited);
        for (DataSonnetIdentifier0 select : selectList) {
            if (curr == null) return null;

            DataSonnetExpr fieldValue = getField(curr, select.getText());
            if (fieldValue == null) return null;

            curr = resolveExprToObj(fieldValue, visited);
        }

        return curr;
    }

    private static DataSonnetObj resolveIdentifierToObj(DataSonnetIdentifier0 id, List<DataSonnetExpr> visited) {
        if (id.getReference() == null) return null;
        PsiElement resolved = id.getReference().resolve();
        if (resolved instanceof DataSonnetBind) {
            DataSonnetExpr expr = ((DataSonnetBind) resolved).getExpr();
            return resolveExprToObj(expr, visited);
        }
        return null;
    }

    private static DataSonnetExpr getField(DataSonnetObj obj, String name) {
        if (obj.getObjinside() == null || obj.getObjinside().getMembers() == null) return null;

        List<DataSonnetMember> memberList = obj.getObjinside().getMembers().getMemberList();
        for (DataSonnetMember member : memberList) {
            if (member.getField() != null) {
                if (member.getField().getFieldname().getIdentifier0() != null) {
                    String fieldName = member.getField().getFieldname().getIdentifier0().getText();
                    if (fieldName.equals(name)) {
                        return member.getField().getExpr();
                    }
                } else if (member.getField().getFieldname() != null) {
                    String fieldName = member.getField().getFieldname().getText();
                    if (fieldName.startsWith("\"") || fieldName.startsWith("\'")) {
                        fieldName = fieldName.substring(1);
                    }
                    if (fieldName.endsWith("\"") || fieldName.endsWith("\'")) {
                        fieldName = fieldName.substring(0, fieldName.length() - 1);
                    }
                    if (fieldName.equals(name)) {
                        return member.getField().getExpr();
                    }
                }
            }
        }
        return null;
    }

    private static DataSonnetObj resolveExpr0ToObj(DataSonnetExpr0 expr0, List<DataSonnetExpr> visited) {
        if (expr0.getExpr() != null){
            return resolveExprToObj(expr0.getExpr(), visited);
        }
        if (expr0.getOuterlocal() != null){
            return resolveExprToObj(expr0.getOuterlocal().getExpr(), visited);
        }
        if (expr0.getObj() != null){
            return expr0.getObj();
        }
        if (expr0.getText().equals("self")) {
            return findSelfObject(expr0);
        }
        if (expr0.getText().equals("$")) {
            return findOuterObject(expr0);
        }
        if (expr0.getImportop() != null) {
            DataSonnetImportop importop = expr0.getImportop();
            if (importop.getReference() == null) {
                return null;
            }
            PsiFile file = (PsiFile) importop.getReference().resolve();
            if (file == null) { // The imported file does not exist
                return null;
            }

            for(PsiElement c: file.getChildren()){
                // Apparently children can be line comments and other unwanted rubbish
                if (c instanceof DataSonnetExpr) {
                    DataSonnetObj res = resolveExprToObj((DataSonnetExpr) c, visited);
                    if (res != null) return res;
                }
            }
        }
        if (expr0.getIdentifier0() != null) {
            return resolveIdentifierToObj(expr0.getIdentifier0(), visited);
        }

        return null;
    }

    private static DataSonnetObj findSelfObject(PsiElement elem) {
        PsiElement curr = elem;
        while (curr != null && !(curr instanceof DataSonnetObj)) {
            curr = curr.getParent();
        }
        return (DataSonnetObj) curr;
    }

    private static DataSonnetObj findOuterObject(PsiElement elem) {
        DataSonnetObj obj = null;
        PsiElement curr = elem;
        while (curr != null) {
            if (curr instanceof DataSonnetObj) {
                obj = (DataSonnetObj) curr;
            }
            curr = curr.getParent();
        }
        return obj;
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
            File[] files = prefixFile.listFiles((dir, name) -> name.startsWith(input));
            if (files != null) {
                for (File f: files) {
                    String result = stripped + f.getName().substring(input.length());
                    replaceSet.addElement(LookupElementBuilder.create(result));
                }
            }
        }
    }
}
