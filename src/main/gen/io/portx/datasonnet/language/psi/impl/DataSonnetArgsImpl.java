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

public class DataSonnetArgsImpl extends ASTWrapperPsiElement implements DataSonnetArgs {

  public DataSonnetArgsImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull DataSonnetVisitor visitor) {
    visitor.visitArgs(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof DataSonnetVisitor) accept((DataSonnetVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<DataSonnetArg> getArgList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, DataSonnetArg.class);
  }

}
