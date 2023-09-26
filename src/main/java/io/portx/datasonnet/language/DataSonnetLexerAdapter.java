package io.portx.datasonnet.language;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

public class DataSonnetLexerAdapter extends FlexAdapter {
    public DataSonnetLexerAdapter() {
        super(new DataSonnetLexer((Reader) null));
    }
}
