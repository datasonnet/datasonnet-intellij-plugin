// This is a generated file. Not intended for manual editing.
package com.modusbox.portx.datasonnet.language.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.modusbox.portx.datasonnet.language.psi.DataSonnetTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.modusbox.portx.datasonnet.language.psi.*;

public class DataSonnetExpr0Impl extends ASTWrapperPsiElement implements DataSonnetExpr0 {

  public DataSonnetExpr0Impl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull DataSonnetVisitor visitor) {
    visitor.visitExpr0(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof DataSonnetVisitor) accept((DataSonnetVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public DataSonnetIdentifier0 getIdentifier0() {
    return findChildByClass(DataSonnetIdentifier0.class);
  }

  @Override
  @Nullable
  public DataSonnetArr getArr() {
    return findChildByClass(DataSonnetArr.class);
  }

  @Override
  @Nullable
  public DataSonnetArrcomp getArrcomp() {
    return findChildByClass(DataSonnetArrcomp.class);
  }

  @Override
  @Nullable
  public DataSonnetAssertStmt getAssertStmt() {
    return findChildByClass(DataSonnetAssertStmt.class);
  }

  @Override
  @Nullable
  public DataSonnetExpr getExpr() {
    return findChildByClass(DataSonnetExpr.class);
  }

  @Override
  @Nullable
  public DataSonnetImportop getImportop() {
    return findChildByClass(DataSonnetImportop.class);
  }

  @Override
  @Nullable
  public DataSonnetImportstrop getImportstrop() {
    return findChildByClass(DataSonnetImportstrop.class);
  }

  @Override
  @Nullable
  public DataSonnetObj getObj() {
    return findChildByClass(DataSonnetObj.class);
  }

  @Override
  @Nullable
  public DataSonnetOuterlocal getOuterlocal() {
    return findChildByClass(DataSonnetOuterlocal.class);
  }

  @Override
  @Nullable
  public DataSonnetParams getParams() {
    return findChildByClass(DataSonnetParams.class);
  }

  @Override
  @Nullable
  public DataSonnetUnaryop getUnaryop() {
    return findChildByClass(DataSonnetUnaryop.class);
  }

}
