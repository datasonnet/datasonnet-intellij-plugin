// This is a generated file. Not intended for manual editing.
package io.portx.datasonnet.language.psi.impl;

import java.util.List;

import io.portx.datasonnet.language.psi.DataSonnetCompspec;
import io.portx.datasonnet.language.psi.DataSonnetForspec;
import io.portx.datasonnet.language.psi.DataSonnetIfspec;
import io.portx.datasonnet.language.psi.DataSonnetVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import io.portx.datasonnet.language.psi.*;

public class DataSonnetCompspecImpl extends ASTWrapperPsiElement implements DataSonnetCompspec {

  public DataSonnetCompspecImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull DataSonnetVisitor visitor) {
    visitor.visitCompspec(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof DataSonnetVisitor) accept((DataSonnetVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<DataSonnetForspec> getForspecList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, DataSonnetForspec.class);
  }

  @Override
  @NotNull
  public List<DataSonnetIfspec> getIfspecList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, DataSonnetIfspec.class);
  }

}
