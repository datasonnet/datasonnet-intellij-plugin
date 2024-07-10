// This is a generated file. Not intended for manual editing.
package io.portx.datasonnet.language.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.portx.datasonnet.language.psi.DataSonnetTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import io.portx.datasonnet.language.psi.*;

public class DataSonnetFieldImpl extends ASTWrapperPsiElement implements DataSonnetField {

  public DataSonnetFieldImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull DataSonnetVisitor visitor) {
    visitor.visitField(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof DataSonnetVisitor) accept((DataSonnetVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public DataSonnetExpr getExpr() {
    return findNotNullChildByClass(DataSonnetExpr.class);
  }

  @Override
  @NotNull
  public DataSonnetFieldname getFieldname() {
    return findNotNullChildByClass(DataSonnetFieldname.class);
  }

  @Override
  @NotNull
  public DataSonnetH getH() {
    return findNotNullChildByClass(DataSonnetH.class);
  }

  @Override
  @Nullable
  public DataSonnetParams getParams() {
    return findChildByClass(DataSonnetParams.class);
  }

}
