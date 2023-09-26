package io.portx.datasonnet.language.formatter;

import com.intellij.application.options.IndentOptionsEditor;
import com.intellij.application.options.SmartIndentOptionsEditor;
import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;
import io.portx.datasonnet.language.DataSonnetLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

import static com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable.SPACES_OTHER;

public class DataSonnetLanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {
    private static final String[] ALIGN_OPTIONS = Arrays.stream(DataSonnetCodeStyleSettings.PropertyAlignment.values())
            .map(alignment -> alignment.getDescription())
            .toArray(value -> new String[value]);

    private static final int[] ALIGN_VALUES =
            ArrayUtil.toIntArray(
                    ContainerUtil.map(DataSonnetCodeStyleSettings.PropertyAlignment.values(), alignment -> alignment.getId()));

    private static final String SAMPLE = "{\n" +
            "    \"DataSonnet literals are\": {\n" +
            "        \"strings\": [\"foo\", \"bar\", \"\\u0062\\u0061\\u0072\"],\n" +
            "        \"numbers\": [42, 6.62606975e-34],\n" +
            "        \"boolean values\": [true, false,],\n" +
            "        \"objects\": {\"null\": null,\"another\": null,}\n" +
            "    }\n" +
            "}";


    @Override
    public void customizeSettings(@NotNull CodeStyleSettingsCustomizable consumer, @NotNull SettingsType settingsType) {
        if (settingsType == SettingsType.SPACING_SETTINGS) {
            consumer.showStandardOptions("SPACE_WITHIN_BRACKETS",
                    "SPACE_WITHIN_BRACES",
                    "SPACE_AFTER_COMMA",
                    "SPACE_BEFORE_COMMA");
            consumer.renameStandardOption("SPACE_WITHIN_BRACES", "Braces");
            consumer.showCustomOption(DataSonnetCodeStyleSettings.class, "SPACE_BEFORE_COLON", "Before ':'", SPACES_OTHER);
            consumer.showCustomOption(DataSonnetCodeStyleSettings.class, "SPACE_AFTER_COLON", "After ':'", SPACES_OTHER);
        }
        else if (settingsType == SettingsType.BLANK_LINES_SETTINGS) {
            consumer.showStandardOptions("KEEP_BLANK_LINES_IN_CODE");
        }
        else if (settingsType == SettingsType.WRAPPING_AND_BRACES_SETTINGS) {
            consumer.showStandardOptions("RIGHT_MARGIN",
                    "WRAP_ON_TYPING",
                    "KEEP_LINE_BREAKS",
                    "WRAP_LONG_LINES");

            consumer.showCustomOption(DataSonnetCodeStyleSettings.class,
                    "KEEP_TRAILING_COMMA",
                    "Trailing comma",
                    CodeStyleSettingsCustomizable.WRAPPING_KEEP);

            consumer.showCustomOption(DataSonnetCodeStyleSettings.class,
                    "ARRAY_WRAPPING",
                    "Arrays",
                    null,
                    CodeStyleSettingsCustomizable.WRAP_OPTIONS,
                    CodeStyleSettingsCustomizable.WRAP_VALUES);

            consumer.showCustomOption(DataSonnetCodeStyleSettings.class,
                    "OBJECT_WRAPPING",
                    "Objects",
                    null,
                    CodeStyleSettingsCustomizable.WRAP_OPTIONS,
                    CodeStyleSettingsCustomizable.WRAP_VALUES);

            consumer.showCustomOption(DataSonnetCodeStyleSettings.class,
                    "PROPERTY_ALIGNMENT",
                    "Align",
                    "Objects",
                    ALIGN_OPTIONS,
                    ALIGN_VALUES);

        }
    }

    @NotNull
    @Override
    public Language getLanguage() {
        return DataSonnetLanguage.INSTANCE;
    }

    @Nullable
    @Override
    public IndentOptionsEditor getIndentOptionsEditor() {
        return new SmartIndentOptionsEditor();
    }

    @Override
    public String getCodeSample(@NotNull SettingsType settingsType) {
        return SAMPLE;
    }

    @Override
    protected void customizeDefaults(@NotNull CommonCodeStyleSettings commonSettings,
                                     @NotNull CommonCodeStyleSettings.IndentOptions indentOptions) {
        indentOptions.INDENT_SIZE = 2;
        // strip all blank lines by default
        commonSettings.KEEP_BLANK_LINES_IN_CODE = 0;
    }
}
