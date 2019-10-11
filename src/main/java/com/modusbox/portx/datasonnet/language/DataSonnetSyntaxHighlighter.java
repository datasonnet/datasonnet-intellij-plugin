package com.modusbox.portx.datasonnet.language;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.modusbox.portx.datasonnet.language.psi.DataSonnetTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class DataSonnetSyntaxHighlighter extends SyntaxHighlighterBase {
    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("DataSonnet_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);
    public static final TextAttributesKey SEPARATOR =
            createTextAttributesKey("DataSonnet_SEPARATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey KEY =
            createTextAttributesKey("DataSonnet_KEY", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey VALUE =
            createTextAttributesKey("DataSonnet_VALUE", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey COMMENT =
            createTextAttributesKey("DataSonnet_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);


    public static final TextAttributesKey NUMBER =
            createTextAttributesKey("DataSonnet_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey NULL_ =
            createTextAttributesKey("DataSonnet_NULL", DefaultLanguageHighlighterColors.CONSTANT);
    public static final TextAttributesKey PARAMS =
            createTextAttributesKey("DataSonnet_PARAMS", DefaultLanguageHighlighterColors.PARAMETER);


    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] SEPARATOR_KEYS = new TextAttributesKey[]{SEPARATOR};
    private static final TextAttributesKey[] KEY_KEYS = new TextAttributesKey[]{KEY};
    private static final TextAttributesKey[] VALUE_KEYS = new TextAttributesKey[]{VALUE};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];
    private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{NUMBER};
    private static final TextAttributesKey[] NULL_KEYS = new TextAttributesKey[]{NULL_};
    private static final TextAttributesKey[] PARAM_KEYS = new TextAttributesKey[]{PARAMS};

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new DataSonnetLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(DataSonnetTypes.COMMA)) {
            return SEPARATOR_KEYS;
        } else if (tokenType.equals(DataSonnetTypes.BLOCK_COMMENT)) {
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
            return KEY_KEYS;
        } else if (tokenType.equals(DataSonnetTypes.NUMBER)) {
            return NUMBER_KEYS;
        } else if (tokenType.equals(DataSonnetTypes.SINGLE_QUOTED_STRING)) {
            return VALUE_KEYS;
        } else if (tokenType.equals(DataSonnetTypes.DOUBLE_QUOTED_STRING)) {
            return VALUE_KEYS;
        } else if (tokenType.equals(DataSonnetTypes.VERBATIM_DOUBLE_QUOTED_STRING)) {
            return VALUE_KEYS;
        } else if (tokenType.equals(DataSonnetTypes.VERBATIM_SINGLE_QUOTED_STRING)) {
            return VALUE_KEYS;
        } else if (tokenType.equals(DataSonnetTypes.TRIPLE_BAR_QUOTED_STRING)) {
            return VALUE_KEYS;
        } else if (tokenType.equals(DataSonnetTypes.PARAMS)) {
            return PARAM_KEYS;
        } else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}