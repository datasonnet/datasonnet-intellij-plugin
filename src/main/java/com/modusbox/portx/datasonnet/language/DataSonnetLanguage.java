package com.modusbox.portx.datasonnet.language;

import com.intellij.lang.Language;

public class DataSonnetLanguage extends Language {
    public static final DataSonnetLanguage INSTANCE = new DataSonnetLanguage();

    private DataSonnetLanguage() {
        super("Datasonnet");
    }
}