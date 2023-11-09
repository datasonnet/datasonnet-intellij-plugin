package io.portx.datasonnet.language;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import io.portx.datasonnet.language.psi.DataSonnetTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class DataSonnetSyntaxHighlighter extends SyntaxHighlighterBase {
    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("DATASONNET_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);

    public static final TextAttributesKey KEY =
            createTextAttributesKey("DATASONNET_KEY", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey VALUE =
            createTextAttributesKey("DATASONNET_VALUE", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey COMMENT =
            createTextAttributesKey("DATASONNET_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey NUMBER =
            createTextAttributesKey("DATASONNET_NUMBER", DefaultLanguageHighlighterColors.NUMBER);


    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEY};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{VALUE};

    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];
    private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{NUMBER};

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new DataSonnetLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(DataSonnetTypes.BLOCK_COMMENT)) {
            return COMMENT_KEYS;
        } else if (tokenType.equals(DataSonnetTypes.LINE_COMMENT)) {
            return COMMENT_KEYS;
        } else if (
                tokenType.equals(DataSonnetTypes.TRUE) ||
                        tokenType.equals(DataSonnetTypes.FALSE) ||
                        tokenType.equals(DataSonnetTypes.NULL) ||
                        tokenType.equals(DataSonnetTypes.IMPORT) ||
                        tokenType.equals(DataSonnetTypes.IMPORTSTR) ||
                        tokenType.equals(DataSonnetTypes.LOCAL) ||
                        tokenType.equals(DataSonnetTypes.FUNCTION) ||
                        tokenType.equals(DataSonnetTypes.IN) ||
                        tokenType.equals(DataSonnetTypes.IF) ||
                        tokenType.equals(DataSonnetTypes.THEN) ||
                        tokenType.equals(DataSonnetTypes.ELSE) ||
                        tokenType.equals(DataSonnetTypes.SUPER) ||
                        tokenType.equals(DataSonnetTypes.ERROR) ||
                        tokenType.equals(DataSonnetTypes.SELF) ||
                        tokenType.equals(DataSonnetTypes.FOR) ||
                        tokenType.equals(DataSonnetTypes.ASSERT) ||
                        tokenType.equals(DataSonnetTypes.DOLLAR)
        ) {
            return KEYWORD_KEYS;
        } else if (tokenType.equals(DataSonnetTypes.NUMBER)) {
            return NUMBER_KEYS;
        } else if (
                tokenType.equals(DataSonnetTypes.SINGLE_QUOTED_STRING) ||
                        tokenType.equals(DataSonnetTypes.DOUBLE_QUOTED_STRING) ||
                        tokenType.equals(DataSonnetTypes.VERBATIM_DOUBLE_QUOTED_STRING) ||
                        tokenType.equals(DataSonnetTypes.VERBATIM_SINGLE_QUOTED_STRING) ||
                        tokenType.equals(DataSonnetTypes.TRIPLE_BAR_QUOTED_STRING)) {
            return STRING_KEYS;
        } else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}