// This is a generated file. Not intended for manual editing.
package io.portx.datasonnet.language.psi.impl;

import io.portx.datasonnet.language.psi.DataSonnetApply;
import io.portx.datasonnet.language.psi.DataSonnetArgs;
import io.portx.datasonnet.language.psi.DataSonnetVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import io.portx.datasonnet.language.psi.*;

public class DataSonnetApplyImpl extends ASTWrapperPsiElement implements DataSonnetApply {

  public DataSonnetApplyImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull DataSonnetVisitor visitor) {
    visitor.visitApply(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof DataSonnetVisitor) accept((DataSonnetVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public DataSonnetArgs getArgs() {
    return findChildByClass(DataSonnetArgs.class);
  }

}
