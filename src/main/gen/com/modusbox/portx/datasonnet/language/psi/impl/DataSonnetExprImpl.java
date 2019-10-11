// This is a generated file. Not intended for manual editing.
package com.modusbox.portx.datasonnet.language.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.modusbox.portx.datasonnet.language.psi.*;

public class DataSonnetExprImpl extends ASTWrapperPsiElement implements DataSonnetExpr {

  public DataSonnetExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull DataSonnetVisitor visitor) {
    visitor.visitExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof DataSonnetVisitor) accept((DataSonnetVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<DataSonnetApply> getApplyList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, DataSonnetApply.class);
  }

  @Override
  @NotNull
  public List<DataSonnetBinsuffix> getBinsuffixList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, DataSonnetBinsuffix.class);
  }

  @Override
  @NotNull
  public DataSonnetExpr0 getExpr0() {
    return findNotNullChildByClass(DataSonnetExpr0.class);
  }

  @Override
  @NotNull
  public List<DataSonnetInsuper> getInsuperList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, DataSonnetInsuper.class);
  }

  @Override
  @NotNull
  public List<DataSonnetObjextend> getObjextendList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, DataSonnetObjextend.class);
  }

  @Override
  @NotNull
  public List<DataSonnetSelect> getSelectList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, DataSonnetSelect.class);
  }

  @Override
  @NotNull
  public List<DataSonnetSlice> getSliceList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, DataSonnetSlice.class);
  }

}
