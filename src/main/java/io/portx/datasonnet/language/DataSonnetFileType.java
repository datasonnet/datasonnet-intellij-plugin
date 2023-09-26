package io.portx.datasonnet.language;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class DataSonnetFileType extends LanguageFileType {
    public static final DataSonnetFileType INSTANCE = new DataSonnetFileType();

    private DataSonnetFileType() {
        super(DataSonnetLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "DataSonnet";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "DataSonnet language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "ds";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return DataSonnetIcon.FILE;
    }

    public List<String> getExtensions() {
        List<String> extensions = new ArrayList<>();
        extensions.add("ds");
        extensions.add("libsonnet");
        extensions.add("ds.TEMPLATE");

        return extensions;
    }
}