// This is a generated file. Not intended for manual editing.
package com.modusbox.portx.jsonnet.language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JsonnetField extends PsiElement {

  @NotNull
  JsonnetExpr getExpr();

  @NotNull
  JsonnetFieldname getFieldname();

  @NotNull
  JsonnetH getH();

  @Nullable
  JsonnetParams getParams();

}
