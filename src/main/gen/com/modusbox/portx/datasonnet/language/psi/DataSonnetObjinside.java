// This is a generated file. Not intended for manual editing.
package com.modusbox.portx.datasonnet.language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface DataSonnetObjinside extends PsiElement {

  @Nullable
  DataSonnetCompspec getCompspec();

  @NotNull
  List<DataSonnetExpr> getExprList();

  @Nullable
  DataSonnetForspec getForspec();

  @Nullable
  DataSonnetMembers getMembers();

  @NotNull
  List<DataSonnetObjlocal> getObjlocalList();

}
