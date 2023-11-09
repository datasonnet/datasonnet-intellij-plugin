// This is a generated file. Not intended for manual editing.
package io.portx.datasonnet.language.psi.impl;

import io.portx.datasonnet.language.psi.DataSonnetIdentifier0;
import io.portx.datasonnet.language.psi.DataSonnetSelect;
import io.portx.datasonnet.language.psi.DataSonnetVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import io.portx.datasonnet.language.psi.*;

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
