package io.portx.datasonnet.language;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DataSonnetLibraryFileType extends LanguageFileType {
    public static final DataSonnetLibraryFileType INSTANCE = new DataSonnetLibraryFileType();

    private DataSonnetLibraryFileType() {
        super(DataSonnetLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "DataSonnet Library";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "DataSonnet library file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "libsonnet";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return DataSonnetIcon.FILE;
    }

//    public List<String> getExtensions() {
//        List<String> extensions = new ArrayList<>();
//        extensions.add("ds");
//        extensions.add("libsonnet");
//        extensions.add("ds.TEMPLATE");
//
//        return extensions;
//    }
}