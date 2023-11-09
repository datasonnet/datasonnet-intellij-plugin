// This is a generated file. Not intended for manual editing.
package io.portx.datasonnet.language.psi.impl;

import io.portx.datasonnet.language.psi.DataSonnetExpr;
import io.portx.datasonnet.language.psi.DataSonnetIfspec;
import io.portx.datasonnet.language.psi.DataSonnetVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import io.portx.datasonnet.language.psi.*;

public class DataSonnetIfspecImpl extends ASTWrapperPsiElement implements DataSonnetIfspec {

  public DataSonnetIfspecImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull DataSonnetVisitor visitor) {
    visitor.visitIfspec(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof DataSonnetVisitor) accept((DataSonnetVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public DataSonnetExpr getExpr() {
    return findNotNullChildByClass(DataSonnetExpr.class);
  }

}
