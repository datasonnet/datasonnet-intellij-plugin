// This is a generated file. Not intended for manual editing.
package com.modusbox.portx.datasonnet.language.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.modusbox.portx.datasonnet.language.psi.*;

public class DataSonnetSelectImpl extends ASTWrapperPsiElement implements DataSonnetSelect {

  public DataSonnetSelectImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull DataSonnetVisitor visitor) {
    visitor.visitSelect(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof DataSonnetVisitor) accept((DataSonnetVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public DataSonnetIdentifier0 getIdentifier0() {
    return findNotNullChildByClass(DataSonnetIdentifier0.class);
  }

}
