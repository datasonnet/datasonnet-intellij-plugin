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

public class DataSonnetMemberImpl extends ASTWrapperPsiElement implements DataSonnetMember {

  public DataSonnetMemberImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull DataSonnetVisitor visitor) {
    visitor.visitMember(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof DataSonnetVisitor) accept((DataSonnetVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public DataSonnetAssertStmt getAssertStmt() {
    return findChildByClass(DataSonnetAssertStmt.class);
  }

  @Override
  @Nullable
  public DataSonnetField getField() {
    return findChildByClass(DataSonnetField.class);
  }

  @Override
  @Nullable
  public DataSonnetObjlocal getObjlocal() {
    return findChildByClass(DataSonnetObjlocal.class);
  }

}
