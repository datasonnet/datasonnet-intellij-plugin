package com.modusbox.portx.datasonnet.language.psi;

import com.intellij.psi.tree.IElementType;
import com.modusbox.portx.datasonnet.language.DataSonnetLanguage;
import org.jetbrains.annotations.*;

public class DataSonnetElementType extends IElementType {
    public DataSonnetElementType(@NotNull @NonNls String debugName) {
        super(debugName, DataSonnetLanguage.INSTANCE);
    }
}