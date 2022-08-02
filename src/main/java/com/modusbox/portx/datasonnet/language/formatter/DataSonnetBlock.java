package com.modusbox.portx.datasonnet.language.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.modusbox.portx.datasonnet.language.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.modusbox.portx.datasonnet.language.DataSonnetParserDefinition.DATASONNET_CONTAINERS;
import static com.modusbox.portx.datasonnet.language.formatter.DataSonnetCodeStyleSettings.ALIGN_PROPERTY_ON_COLON;
import static com.modusbox.portx.datasonnet.language.formatter.DataSonnetCodeStyleSettings.ALIGN_PROPERTY_ON_VALUE;
import static com.modusbox.portx.datasonnet.language.psi.DataSonnetTypes.*;

public class DataSonnetBlock implements ASTBlock {
    private static final TokenSet DATASONNET_OPEN_BRACES = TokenSet.create(L_BRACKET, L_CURLY);
    private static final TokenSet DATASONNET_CLOSE_BRACES = TokenSet.create(R_BRACKET, R_CURLY);
    private static final TokenSet DATASONNET_ALL_BRACES = TokenSet.orSet(DATASONNET_OPEN_BRACES, DATASONNET_CLOSE_BRACES);

    private final DataSonnetBlock myParent;

    private final ASTNode myNode;
    private final PsiElement myPsiElement;
    private final Alignment myAlignment;
    private final Indent myIndent;
    private final Wrap myWrap;
    private final DataSonnetCodeStyleSettings myCustomSettings;
    private final SpacingBuilder mySpacingBuilder;
    // lazy initialized on first call to #getSubBlocks()
    private List<Block> mySubBlocks = null;

    private final Alignment myPropertyValueAlignment;
    private final Wrap myChildWrap;

    /**
     * @deprecated Please use overload with settings DataSonnetCodeStyleSettings and spacingBuilder.
     * Getting settings should be done only for the root block.
     */
    @Deprecated
    @SuppressWarnings("unused") //used externally
    public DataSonnetBlock(@Nullable DataSonnetBlock parent,
                     @NotNull ASTNode node,
                     @NotNull CodeStyleSettings settings,
                     @Nullable Alignment alignment,
                     @NotNull Indent indent,
                     @Nullable Wrap wrap) {
        this(parent, node, settings.getCustomSettings(DataSonnetCodeStyleSettings.class), alignment, indent, wrap,
                DataSonnetFormattingModelBuilder.createSpacingBuilder(settings));
    }

    public DataSonnetBlock(@Nullable DataSonnetBlock parent,
                     @NotNull ASTNode node,
                     @NotNull DataSonnetCodeStyleSettings customSettings,
                     @Nullable Alignment alignment,
                     @NotNull Indent indent,
                     @Nullable Wrap wrap,
                     @NotNull SpacingBuilder spacingBuilder) {
        myParent = parent;
        myNode = node;
        myPsiElement = node.getPsi();
        myAlignment = alignment;
        myIndent = indent;
        myWrap = wrap;
        mySpacingBuilder = spacingBuilder;
        myCustomSettings = customSettings;

        if (myPsiElement instanceof DataSonnetObj) {
            myChildWrap = Wrap.createWrap(myCustomSettings.OBJECT_WRAPPING, true);
        }
        else if (myPsiElement instanceof DataSonnetArr || myPsiElement instanceof DataSonnetArrcomp) {
            myChildWrap = Wrap.createWrap(myCustomSettings.ARRAY_WRAPPING, true);
        }
        else {
            myChildWrap = null;
        }

        myPropertyValueAlignment = myPsiElement instanceof DataSonnetObj ||
                                   myPsiElement instanceof DataSonnetField ||
                                   myPsiElement instanceof DataSonnetExpr ||
                                   myPsiElement instanceof DataSonnetMember ? Alignment.createAlignment(true) : null;
        //myPropertyValueAlignment = Alignment.createAlignment(true);
    }

    @Override
    public ASTNode getNode() {
        return myNode;
    }

    @NotNull
    @Override
    public TextRange getTextRange() {
        return myNode.getTextRange();
    }

    @NotNull
    @Override
    public List<Block> getSubBlocks() {
        if (mySubBlocks == null) {
            int propertyAlignment = myCustomSettings.PROPERTY_ALIGNMENT;
            ASTNode[] children = myNode.getChildren(null);
            mySubBlocks = new ArrayList<>(children.length);
            for (ASTNode child: children) {
                if (isWhitespaceOrEmpty(child)) continue;
                mySubBlocks.add(makeSubBlock(child, propertyAlignment));
            }
        }
        return mySubBlocks;
    }

    private Block makeSubBlock(@NotNull ASTNode childNode, int propertyAlignment) {
        Indent indent = Indent.getNoneIndent();
        Alignment alignment = null;
        Wrap wrap = null;

        if (hasElementType(myNode, DATASONNET_CONTAINERS)) {
            if (hasElementType(childNode, COMMA)) {
                wrap = Wrap.createWrap(WrapType.NONE, true);
            }
            else if (!hasElementType(childNode, DATASONNET_ALL_BRACES)) {
                assert myChildWrap != null;
                wrap = myChildWrap;
                indent = Indent.getNormalIndent();
            }
            else if (hasElementType(childNode, DATASONNET_OPEN_BRACES) && myParent != null) {
                if (isPropertyValue(myPsiElement) && propertyAlignment == ALIGN_PROPERTY_ON_VALUE) {
                    // WEB-13587 Align compound values on opening brace/bracket, not the whole block
                    assert myParent != null && myParent.myParent != null && myParent.myParent.myPropertyValueAlignment != null;
                    alignment = myParent.myParent.myPropertyValueAlignment;
                }
            }
        } else if (hasElementType(childNode, IF, THEN, ELSE) && myParent != null) {//TODO this doesn't always produce good results, needs rethinking
            alignment = myParent.myPropertyValueAlignment;
            wrap = myParent.myWrap;
            indent = myParent.myIndent;
            //System.out.println("I am " + myPsiElement.getText());
            // System.out.println("My Parent " + myParent.myPsiElement.getText());
        } else if (hasElementType(myNode, FIELD) ) {
            assert myParent != null && myParent.myPropertyValueAlignment != null;
            if (hasElementType(childNode, COLON) && propertyAlignment == ALIGN_PROPERTY_ON_COLON) {
                alignment = myParent.myPropertyValueAlignment;
            }
            else if (isPropertyValue(childNode.getPsi()) && propertyAlignment == ALIGN_PROPERTY_ON_VALUE) {
                if (!hasElementType(childNode, DATASONNET_CONTAINERS)) {
                    alignment = myParent.myPropertyValueAlignment;
                }
            }
        }
        return new DataSonnetBlock(this, childNode, myCustomSettings, alignment, indent, wrap, mySpacingBuilder);
    }

    @Nullable
    @Override
    public Wrap getWrap() {
        return myWrap;
    }

    @Nullable
    @Override
    public Indent getIndent() {
        return myIndent;
    }

    @Nullable
    @Override
    public Alignment getAlignment() {
        return myAlignment;
    }

    @Nullable
    @Override
    public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
        return mySpacingBuilder.getSpacing(this, child1, child2);
    }

    @NotNull
    @Override
    public ChildAttributes getChildAttributes(int newChildIndex) {
        if (hasElementType(myNode, DATASONNET_CONTAINERS)) {
            // WEB-13675: For some reason including alignment in child attributes causes
            // indents to consist solely of spaces when both USE_TABS and SMART_TAB
            // options are enabled.
            return new ChildAttributes(Indent.getNormalIndent(), null);
        }
        else if (myNode.getPsi() instanceof PsiFile) {
            return new ChildAttributes(Indent.getNoneIndent(), null);
        }
        // Will use continuation indent for cases like { "foo"<caret>  }
        return new ChildAttributes(null, null);
    }

    @Override
    public boolean isIncomplete() {
        final ASTNode lastChildNode = myNode.getLastChildNode();
        if (hasElementType(myNode, OBJ)) {
            return lastChildNode != null && lastChildNode.getElementType() != R_CURLY;
        }
        else if (hasElementType(myNode, ARR)) {
            return lastChildNode != null && lastChildNode.getElementType() != R_BRACKET;
        }
        else if (hasElementType(myNode, FIELD)) {
            return ((DataSonnetField)myPsiElement).getExpr() == null;
        }
        return false;
    }

    @Override
    public boolean isLeaf() {
        return myNode.getFirstChildNode() == null;
    }

    private static boolean isWhitespaceOrEmpty(ASTNode node) {
        return node.getElementType() == TokenType.WHITE_SPACE || node.getTextLength() == 0;
    }

    /**
     * Check that element type of the given AST node belongs to the token set.
     * <p/>
     * It slightly less verbose than {@code set.contains(node.getElementType())} and overloaded methods with the same name
     * allow check ASTNode/PsiElement against both concrete element types and token sets in uniform way.
     */
    private boolean hasElementType(@NotNull ASTNode node, @NotNull TokenSet set) {
        return set.contains(node.getElementType());
    }

    /**
     * @see #hasElementType(com.intellij.lang.ASTNode, com.intellij.psi.tree.TokenSet)
     */
    private boolean hasElementType(@NotNull ASTNode node, IElementType... types) {
        return hasElementType(node, TokenSet.create(types));
    }

    /**
     * Checks that PSI element represents value of JSON property (key-value pair of JSON object)
     *
     * @param element PSI element to check
     * @return whether this PSI element is property value
     */
    private boolean isPropertyValue(@NotNull PsiElement element) {
        final PsiElement parent = element.getParent();
        return parent instanceof DataSonnetField && element == ((DataSonnetField)parent).getExpr();
    }

    private boolean hasOuterLocalParent() {
        DataSonnetBlock parentBlock = myParent;
        while (parentBlock != null) {
            if (parentBlock.myPsiElement instanceof DataSonnetOuterlocal) {
                return true;
            }
        }
        return false;
    }
}
