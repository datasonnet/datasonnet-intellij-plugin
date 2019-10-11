package com.modusbox.portx.datasonnet.language.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.modusbox.portx.datasonnet.language.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class DataSonnetFile extends PsiFileBase {
    public DataSonnetFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, DataSonnetLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return DataSonnetFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "DataSonnet File";
    }

    @Override
    public Icon getIcon(int flags) {
        return super.getIcon(flags);
    }
}