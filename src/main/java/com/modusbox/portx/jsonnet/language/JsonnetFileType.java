package com.modusbox.portx.jsonnet.language;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class JsonnetFileType extends LanguageFileType {
    public static final JsonnetFileType INSTANCE = new JsonnetFileType();

    private JsonnetFileType() {
        super(JsonnetLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Datasonnet file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Datasonnet language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "ds";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return JsonnetIcon.FILE;
    }

    public List<String> getExtensions() {
        List<String> extensions = new ArrayList<>();
        extensions.add("ds");
        extensions.add("libsonnet");
        extensions.add("ds.TEMPLATE");

        return extensions;
    }
}