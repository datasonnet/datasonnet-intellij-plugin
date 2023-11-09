// This is a generated file. Not intended for manual editing.
package io.portx.datasonnet.language.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface DataSonnetField extends PsiElement {

  @NotNull
  DataSonnetExpr getExpr();

  @NotNull
  DataSonnetFieldname getFieldname();

  @NotNull
  DataSonnetH getH();

  @Nullable
  DataSonnetParams getParams();

}
