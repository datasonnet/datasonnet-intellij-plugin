// This is a generated file. Not intended for manual editing.
package com.modusbox.portx.datasonnet.language.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.modusbox.portx.datasonnet.language.psi.*;

public class DataSonnetMemberImpl extends ASTWrapperPsiElement implements DataSonnetMember {

  public DataSonnetMemberImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull DataSonnetVisitor visitor) {
    visitor.visitMember(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof DataSonnetVisitor) accept((DataSonnetVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public DataSonnetAssertStmt getAssertStmt() {
    return findChildByClass(DataSonnetAssertStmt.class);
  }

  @Override
  @Nullable
  public DataSonnetField getField() {
    return findChildByClass(DataSonnetField.class);
  }

  @Override
  @Nullable
  public DataSonnetObjlocal getObjlocal() {
    return findChildByClass(DataSonnetObjlocal.class);
  }

}
