// This is a generated file. Not intended for manual editing.
package io.portx.datasonnet.language.psi.impl;

import java.util.List;

import io.portx.datasonnet.language.psi.DataSonnetArr;
import io.portx.datasonnet.language.psi.DataSonnetExpr;
import io.portx.datasonnet.language.psi.DataSonnetVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import io.portx.datasonnet.language.psi.*;

public class DataSonnetArrImpl extends ASTWrapperPsiElement implements DataSonnetArr {

  public DataSonnetArrImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull DataSonnetVisitor visitor) {
    visitor.visitArr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof DataSonnetVisitor) accept((DataSonnetVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<DataSonnetExpr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, DataSonnetExpr.class);
  }

}
