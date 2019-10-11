// This is a generated file. Not intended for manual editing.
package com.modusbox.portx.datasonnet.language.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.modusbox.portx.datasonnet.language.psi.*;

public class DataSonnetObjinsideImpl extends ASTWrapperPsiElement implements DataSonnetObjinside {

  public DataSonnetObjinsideImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull DataSonnetVisitor visitor) {
    visitor.visitObjinside(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof DataSonnetVisitor) accept((DataSonnetVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public DataSonnetCompspec getCompspec() {
    return findChildByClass(DataSonnetCompspec.class);
  }

  @Override
  @NotNull
  public List<DataSonnetExpr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, DataSonnetExpr.class);
  }

  @Override
  @Nullable
  public DataSonnetForspec getForspec() {
    return findChildByClass(DataSonnetForspec.class);
  }

  @Override
  @NotNull
  public List<DataSonnetMember> getMemberList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, DataSonnetMember.class);
  }

  @Override
  @NotNull
  public List<DataSonnetObjlocal> getObjlocalList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, DataSonnetObjlocal.class);
  }

}
