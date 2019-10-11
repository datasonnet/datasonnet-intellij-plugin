// This is a generated file. Not intended for manual editing.
package com.modusbox.portx.datasonnet.language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface DataSonnetExpr extends PsiElement {

  @NotNull
  List<DataSonnetApply> getApplyList();

  @NotNull
  List<DataSonnetBinsuffix> getBinsuffixList();

  @NotNull
  DataSonnetExpr0 getExpr0();

  @NotNull
  List<DataSonnetInsuper> getInsuperList();

  @NotNull
  List<DataSonnetObjextend> getObjextendList();

  @NotNull
  List<DataSonnetSelect> getSelectList();

  @NotNull
  List<DataSonnetSlice> getSliceList();

}
