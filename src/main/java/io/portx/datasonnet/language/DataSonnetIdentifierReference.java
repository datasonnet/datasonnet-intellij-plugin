package io.portx.datasonnet.language;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import io.portx.datasonnet.language.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DataSonnetIdentifierReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {

    public DataSonnetIdentifierReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        List<ResolveResult> results = new ArrayList<>();
        String identifier = this.getElement().getText();
        PsiElement element = this.getElement();
        while (element != null) {
            if (element instanceof DataSonnetSelect) {
                List<DataSonnetIdentifier0> selectList = new ArrayList<>();
                for (DataSonnetSelect select : ((DataSonnetExpr) element.getParent()).getSelectList()) {
                    if (select == element) {
                        break;
                    }
                    selectList.add(select.getIdentifier0());
                }
                DataSonnetObj obj = DataSonnetCompletionContributor.resolveExprToObj(
                        (DataSonnetExpr) element.getParent(),
                        new ArrayList<>(),
                        selectList
                );
                if (obj != null) {

                    if (obj.getObjinside() != null && obj.getObjinside().getMembers() != null) {
                        String lastSelectText = ((DataSonnetSelect) element).getIdentifier0().getText();
                        DataSonnetMembers members = obj.getObjinside().getMembers();
                        for (DataSonnetMember m : members.getMemberList()) {
                            if (m.getField() != null) {
                                DataSonnetIdentifier0 ident = m.getField().getFieldname().getIdentifier0();
                                if (ident != null && ident.getText().equals(lastSelectText)) {
                                    results.add(new PsiElementResolveResult(m.getField().getFieldname().getIdentifier0()));
                                    return results.toArray(new ResolveResult[results.size()]);
                                }
                            }
                        }
                    }

                    results.add(new PsiElementResolveResult(obj));
                    return results.toArray(new ResolveResult[results.size()]);
                }

            } else if (element instanceof DataSonnetOuterlocal) {
                List<DataSonnetBind> binds = findBindInOuterLocal((DataSonnetOuterlocal) element);
                for (DataSonnetBind b : binds) {
                    if (identifier.equals(findIdentifierFromBind(b))) {
                        results.add(new PsiElementResolveResult(b));
                        return results.toArray(new ResolveResult[results.size()]);
                    }
                }
            } else if (element instanceof DataSonnetExpr0 && isFunctionExpr((DataSonnetExpr0) element)) {
                List<DataSonnetIdentifier0> identifiers = findIdentifierFromFunctionExpr0((DataSonnetExpr0) element);
                for (DataSonnetIdentifier0 i : identifiers) {
                    if (identifier.equals(i.getText())) {
                        results.add(new PsiElementResolveResult(i));
                        return results.toArray(new ResolveResult[results.size()]);
                    }
                }
            } else if (element instanceof DataSonnetObjinside) {

                List<DataSonnetObjlocal> locals = new ArrayList<>(((DataSonnetObjinside) element).getObjlocalList());
                DataSonnetMembers members = ((DataSonnetObjinside) element).getMembers();
                if (members != null) {
                    for (DataSonnetMember m : members.getMemberList()) {
                        if (m.getObjlocal() != null) {
                            locals.add(m.getObjlocal());
                        }
                    }
                }
                for (DataSonnetObjlocal local : locals) {
                    DataSonnetBind b = local.getBind();
                    if (identifier.equals(findIdentifierFromBind(b))) {
                        results.add(new PsiElementResolveResult(b));
                        return results.toArray(new ResolveResult[results.size()]);
                    }
                }

                DataSonnetCompspec comp = ((DataSonnetObjinside) element).getCompspec();
                DataSonnetForspec forspec = ((DataSonnetObjinside) element).getForspec();
                if (findComprehensionBinding(results, identifier, comp, forspec))
                    return results.toArray(new ResolveResult[results.size()]);

            } else if (element instanceof DataSonnetArrcomp) {
                DataSonnetCompspec comp = ((DataSonnetArrcomp) element).getCompspec();
                DataSonnetForspec forspec = ((DataSonnetArrcomp) element).getForspec();
                if (findComprehensionBinding(results, identifier, comp, forspec))
                    return results.toArray(new ResolveResult[results.size()]);
            } else if (element.getParent() instanceof DataSonnetBind &&
                    ((DataSonnetBind) element.getParent()).getExpr() == element) {
                List<DataSonnetIdentifier0> idents = new ArrayList<>(findIdentifierFromParams(((DataSonnetBind) element.getParent()).getParams()));
                for (DataSonnetIdentifier0 ident : idents) {
                    if (identifier.equals(ident.getText())) {
                        results.add(new PsiElementResolveResult(ident));
                        return results.toArray(new ResolveResult[results.size()]);
                    }
                }
            } else if (element.getParent() instanceof DataSonnetField &&
                    ((DataSonnetField) element.getParent()).getExpr() == element) {
                List<DataSonnetIdentifier0> idents = new ArrayList<>(findIdentifierFromParams(((DataSonnetField) element.getParent()).getParams()));
                for (DataSonnetIdentifier0 ident : idents) {
                    if (identifier.equals(ident.getText())) {
                        results.add(new PsiElementResolveResult(ident));
                        return results.toArray(new ResolveResult[results.size()]);
                    }
                }
            }
            element = element.getParent();
        }
        return results.toArray(new ResolveResult[results.size()]);
    }

    private boolean findComprehensionBinding(List<ResolveResult> results, String identifier, DataSonnetCompspec comp, DataSonnetForspec forspec) {
        if (comp != null) {

            for (DataSonnetForspec spec : comp.getForspecList()) {
                if (identifier.equals(spec.getIdentifier0().getText())) {
                    results.add(new PsiElementResolveResult(spec.getIdentifier0()));
                    return true;
                }
            }
        }
        if (forspec != null) {
            if (identifier.equals(forspec.getIdentifier0().getText())) {
                results.add(new PsiElementResolveResult(forspec.getIdentifier0()));
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new LookupElement[]{};
    }

    public static List<DataSonnetBind> findBindInOuterLocal(DataSonnetOuterlocal element) {
        List<DataSonnetBind> ret = new ArrayList<>();
        if (element instanceof DataSonnetExpr) {
            return new ArrayList<>();
        }
        for (PsiElement child : element.getChildren()) {
            if (child instanceof DataSonnetBind) {
                ret.add((DataSonnetBind) child);
            }
        }
        return ret;
    }

    public static String findIdentifierFromBind(DataSonnetBind element) {
        return element.getFirstChild().getText();
    }

    public static List<DataSonnetIdentifier0> findIdentifierFromFunctionExpr0(DataSonnetExpr0 element) {

        PsiElement params = null;
        for (PsiElement c : element.getChildren()) {
            if (c instanceof DataSonnetParams) params = c;
        }

        return findIdentifierFromParams((DataSonnetParams) params);
    }

    public static List<DataSonnetIdentifier0> findIdentifierFromParams(DataSonnetParams params) {
        List<DataSonnetIdentifier0> ret = new ArrayList<>();
        if (params == null) return ret;
        for (PsiElement child : params.getChildren()) {
            if (child instanceof DataSonnetParam) {
                PsiElement identifier = child.getFirstChild();
                if (!(identifier instanceof DataSonnetIdentifier0)) continue;
                ret.add((DataSonnetIdentifier0) identifier);
            }
        }
        return ret;
    }

    public static boolean isFunctionExpr(DataSonnetExpr0 element) {
        if (element.getFirstChild() instanceof LeafPsiElement) {
            return ((LeafPsiElement) element.getFirstChild()).getElementType().equals(DataSonnetTypes.FUNCTION);
        }
        return false;
    }

}
