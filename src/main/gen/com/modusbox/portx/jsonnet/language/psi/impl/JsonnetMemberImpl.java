// This is a generated file. Not intended for manual editing.
package com.modusbox.portx.jsonnet.language.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.modusbox.portx.jsonnet.language.psi.JsonnetTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.modusbox.portx.jsonnet.language.psi.*;

public class JsonnetMemberImpl extends ASTWrapperPsiElement implements JsonnetMember {

  public JsonnetMemberImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JsonnetVisitor visitor) {
    visitor.visitMember(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JsonnetVisitor) accept((JsonnetVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public JsonnetAssertStmt getAssertStmt() {
    return findChildByClass(JsonnetAssertStmt.class);
  }

  @Override
  @Nullable
  public JsonnetField getField() {
    return findChildByClass(JsonnetField.class);
  }

  @Override
  @Nullable
  public JsonnetObjlocal getObjlocal() {
    return findChildByClass(JsonnetObjlocal.class);
  }

}
