package io.portx.datasonnet.language.formatter;

import com.intellij.formatting.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import io.portx.datasonnet.language.DataSonnetLanguage;
import org.jetbrains.annotations.NotNull;

import static io.portx.datasonnet.language.psi.DataSonnetTypes.*;

public class DataSonnetFormattingModelBuilder implements FormattingModelBuilder {
    @NotNull
    @Override
    public FormattingModel createModel(PsiElement element, CodeStyleSettings settings) {
        DataSonnetCodeStyleSettings customSettings = settings.getCustomSettings(DataSonnetCodeStyleSettings.class);
        SpacingBuilder spacingBuilder = createSpacingBuilder(settings);
        final DataSonnetBlock block = new DataSonnetBlock(null, element.getNode(), customSettings, null, Indent.getSmartIndent(Indent.Type.CONTINUATION), null, spacingBuilder);
        return FormattingModelProvider.createFormattingModelForPsiFile(element.getContainingFile(), block, settings);
    }

    @NotNull
    static SpacingBuilder createSpacingBuilder(CodeStyleSettings settings) {
        final DataSonnetCodeStyleSettings jsonSettings = settings.getCustomSettings(DataSonnetCodeStyleSettings.class);
        final CommonCodeStyleSettings commonSettings = settings.getCommonSettings(DataSonnetLanguage.INSTANCE);

        final int spacesBeforeComma = commonSettings.SPACE_BEFORE_COMMA ? 1 : 0;
        final int spacesBeforeColon = jsonSettings.SPACE_BEFORE_COLON ? 1 : 0;
        final int spacesAfterColon = jsonSettings.SPACE_AFTER_COLON ? 1 : 0;

        return new SpacingBuilder(settings, DataSonnetLanguage.INSTANCE)
                .before(COLON).spacing(spacesBeforeColon, spacesBeforeColon, 0, false, 0)
                .after(COLON).spacing(spacesAfterColon, spacesAfterColon, 0, false, 0)
                .withinPair(L_BRACKET, R_BRACKET).spaceIf(commonSettings.SPACE_WITHIN_BRACKETS, true)
                .withinPair(L_CURLY, R_CURLY).spaceIf(commonSettings.SPACE_WITHIN_BRACES, true)
                .before(COMMA).spacing(spacesBeforeComma, spacesBeforeComma, 0, false, 0)
                .after(COMMA).spaceIf(commonSettings.SPACE_AFTER_COMMA);
    }
}
