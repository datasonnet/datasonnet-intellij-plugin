package com.modusbox.portx.datasonnet.language;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.modusbox.portx.datasonnet.language.psi.DataSonnetTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DataSonnetBraceMatcher implements PairedBraceMatcher {

    private static final BracePair[] PAIRS = new BracePair[]{
            new BracePair(DataSonnetTypes.L_BRACKET, DataSonnetTypes.R_BRACKET, false),
            new BracePair(DataSonnetTypes.L_PAREN, DataSonnetTypes.R_PAREN, false),
            new BracePair(DataSonnetTypes.L_CURLY, DataSonnetTypes.R_CURLY, true),
    };

    @NotNull
    public BracePair[] getPairs() {
        return PAIRS;
    }

    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType type, @Nullable IElementType tokenType) {
        return false;
    }

    public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
        return openingBraceOffset;
    }
}
