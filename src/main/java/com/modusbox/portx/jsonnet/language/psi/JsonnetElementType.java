package com.modusbox.portx.jsonnet.language.psi;

import com.intellij.psi.tree.IElementType;
import com.modusbox.portx.jsonnet.language.JsonnetLanguage;
import org.jetbrains.annotations.*;

public class JsonnetElementType extends IElementType {
    public JsonnetElementType(@NotNull @NonNls String debugName) {
        super(debugName, JsonnetLanguage.INSTANCE);
    }
}