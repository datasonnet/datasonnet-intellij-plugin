package io.portx.datasonnet.language.psi;

import com.intellij.psi.tree.IElementType;
import io.portx.datasonnet.language.DataSonnetLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class DataSonnetElementType extends IElementType {
    public DataSonnetElementType(@NotNull @NonNls String debugName) {
        super(debugName, DataSonnetLanguage.INSTANCE);
    }
}