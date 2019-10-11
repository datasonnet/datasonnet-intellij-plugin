// This is a generated file. Not intended for manual editing.
package com.modusbox.portx.datasonnet.language.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.modusbox.portx.datasonnet.language.psi.*;

public class DataSonnetArgsImpl extends ASTWrapperPsiElement implements DataSonnetArgs {

  public DataSonnetArgsImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull DataSonnetVisitor visitor) {
    visitor.visitArgs(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof DataSonnetVisitor) accept((DataSonnetVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<DataSonnetArg> getArgList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, DataSonnetArg.class);
  }

}
