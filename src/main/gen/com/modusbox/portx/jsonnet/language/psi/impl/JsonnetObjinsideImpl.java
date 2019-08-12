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

public class JsonnetObjinsideImpl extends ASTWrapperPsiElement implements JsonnetObjinside {

  public JsonnetObjinsideImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JsonnetVisitor visitor) {
    visitor.visitObjinside(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JsonnetVisitor) accept((JsonnetVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public JsonnetCompspec getCompspec() {
    return findChildByClass(JsonnetCompspec.class);
  }

  @Override
  @NotNull
  public List<JsonnetExpr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JsonnetExpr.class);
  }

  @Override
  @Nullable
  public JsonnetForspec getForspec() {
    return findChildByClass(JsonnetForspec.class);
  }

  @Override
  @NotNull
  public List<JsonnetMember> getMemberList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JsonnetMember.class);
  }

  @Override
  @NotNull
  public List<JsonnetObjlocal> getObjlocalList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JsonnetObjlocal.class);
  }

}
