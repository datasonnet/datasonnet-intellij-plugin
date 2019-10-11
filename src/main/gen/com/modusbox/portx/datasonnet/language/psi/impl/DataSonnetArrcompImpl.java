// This is a generated file. Not intended for manual editing.
package com.modusbox.portx.datasonnet.language.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.modusbox.portx.datasonnet.language.psi.*;

public class DataSonnetArrcompImpl extends ASTWrapperPsiElement implements DataSonnetArrcomp {

  public DataSonnetArrcompImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull DataSonnetVisitor visitor) {
    visitor.visitArrcomp(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof DataSonnetVisitor) accept((DataSonnetVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public DataSonnetCompspec getCompspec() {
    return findNotNullChildByClass(DataSonnetCompspec.class);
  }

  @Override
  @NotNull
  public DataSonnetExpr getExpr() {
    return findNotNullChildByClass(DataSonnetExpr.class);
  }

  @Override
  @NotNull
  public DataSonnetForspec getForspec() {
    return findNotNullChildByClass(DataSonnetForspec.class);
  }

}
