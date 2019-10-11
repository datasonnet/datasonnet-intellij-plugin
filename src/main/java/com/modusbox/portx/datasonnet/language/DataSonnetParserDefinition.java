package com.modusbox.portx.datasonnet.language;

import com.intellij.lang.*;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.modusbox.portx.datasonnet.language.parser.DataSonnetParser;
import com.modusbox.portx.datasonnet.language.psi.*;
import org.jetbrains.annotations.NotNull;

public class DataSonnetParserDefinition implements ParserDefinition {
    public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    public static final TokenSet COMMENTS = TokenSet.create(
            DataSonnetTypes.LINE_COMMENT, DataSonnetTypes.BLOCK_COMMENT);

    public static final IFileElementType FILE = new IFileElementType(DataSonnetLanguage.INSTANCE);

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new DataSonnetLexerAdapter();
    }

    @NotNull
    public TokenSet getWhitespaceTokens() {
        return WHITE_SPACES;
    }

    @NotNull
    public TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @NotNull
    public TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @NotNull
    public PsiParser createParser(final Project project) {
        return new DataSonnetParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    public PsiFile createFile(FileViewProvider viewProvider) {
        return new DataSonnetFile(viewProvider);
    }

    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }

    @NotNull
    public PsiElement createElement(ASTNode node) {
        return DataSonnetTypes.Factory.createElement(node);
    }
}