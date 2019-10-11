// This is a generated file. Not intended for manual editing.
package com.modusbox.portx.datasonnet.language.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.modusbox.portx.datasonnet.language.psi.*;

public class DataSonnetOuterlocalImpl extends ASTWrapperPsiElement implements DataSonnetOuterlocal {

  public DataSonnetOuterlocalImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull DataSonnetVisitor visitor) {
    visitor.visitOuterlocal(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof DataSonnetVisitor) accept((DataSonnetVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<DataSonnetBind> getBindList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, DataSonnetBind.class);
  }

  @Override
  @NotNull
  public DataSonnetExpr getExpr() {
    return findNotNullChildByClass(DataSonnetExpr.class);
  }

}
