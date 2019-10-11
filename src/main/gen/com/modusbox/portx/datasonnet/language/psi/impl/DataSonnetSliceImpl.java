// This is a generated file. Not intended for manual editing.
package com.modusbox.portx.datasonnet.language.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.modusbox.portx.datasonnet.language.psi.*;

public class DataSonnetSliceImpl extends ASTWrapperPsiElement implements DataSonnetSlice {

  public DataSonnetSliceImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull DataSonnetVisitor visitor) {
    visitor.visitSlice(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof DataSonnetVisitor) accept((DataSonnetVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public DataSonnetExpr getExpr() {
    return findChildByClass(DataSonnetExpr.class);
  }

  @Override
  @Nullable
  public DataSonnetSlicesuffix getSlicesuffix() {
    return findChildByClass(DataSonnetSlicesuffix.class);
  }

}
