package io.portx.datasonnet.language.psi;

import com.intellij.psi.tree.IElementType;
import io.portx.datasonnet.language.DataSonnetLanguage;
import org.jetbrains.annotations.*;

public class DataSonnetTokenType extends IElementType {
    public DataSonnetTokenType(@NotNull @NonNls String debugName) {
        super(debugName, DataSonnetLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "DataSonnetTokenType." + super.toString();
    }
}