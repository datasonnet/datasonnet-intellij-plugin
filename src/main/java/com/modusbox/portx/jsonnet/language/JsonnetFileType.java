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
        return "Jsonnet file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Jsonnet language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "jsonnet";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return JsonnetIcon.FILE;
    }

    public List<String> getExtensions() {
        List<String> extensions = new ArrayList<>();
        extensions.add("jsonnet");
        extensions.add("libsonnet");
        extensions.add("jsonnet.TEMPLATE");

        return extensions;
    }
}