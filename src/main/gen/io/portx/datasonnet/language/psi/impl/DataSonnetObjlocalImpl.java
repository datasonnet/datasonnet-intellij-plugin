// This is a generated file. Not intended for manual editing.
package io.portx.datasonnet.language.psi.impl;

import io.portx.datasonnet.language.psi.DataSonnetBind;
import io.portx.datasonnet.language.psi.DataSonnetObjlocal;
import io.portx.datasonnet.language.psi.DataSonnetVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import io.portx.datasonnet.language.psi.*;

public class DataSonnetObjlocalImpl extends ASTWrapperPsiElement implements DataSonnetObjlocal {

  public DataSonnetObjlocalImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull DataSonnetVisitor visitor) {
    visitor.visitObjlocal(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof DataSonnetVisitor) accept((DataSonnetVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public DataSonnetBind getBind() {
    return findNotNullChildByClass(DataSonnetBind.class);
  }

}
