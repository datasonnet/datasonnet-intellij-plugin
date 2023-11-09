package io.portx.datasonnet.language.formatter;

import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CodeStyleConfigurable;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import io.portx.datasonnet.language.DataSonnetLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DataSonnetCodeStyleSettingsProvider extends CodeStyleSettingsProvider {

    @NotNull
    public CodeStyleConfigurable createConfigurable(@NotNull CodeStyleSettings settings, @NotNull CodeStyleSettings modelSettings) {
        return new CodeStyleAbstractConfigurable(settings, modelSettings, this.getConfigurableDisplayName()) {
            @Override
            protected CodeStyleAbstractPanel createPanel(CodeStyleSettings settings) {
                final Language language = DataSonnetLanguage.INSTANCE;
                final CodeStyleSettings currentSettings = getCurrentSettings();
                return new TabbedLanguageCodeStylePanel(language, currentSettings, settings) {
                    @Override
                    protected void initTabs(CodeStyleSettings settings) {
                        addIndentOptionsTab(settings);
                        addSpacesTab(settings);
                        addBlankLinesTab(settings);
                        addWrappingAndBracesTab(settings);
                    }
                };
            }
        };
    }

/*
    @NotNull
    @Override
    public Configurable createSettingsPage(CodeStyleSettings settings, CodeStyleSettings originalSettings) {
        return new CodeStyleAbstractConfigurable(settings, originalSettings, "DataSonnet") {
            @Override
            protected CodeStyleAbstractPanel createPanel(CodeStyleSettings settings) {
                final Language language = DataSonnetLanguage.INSTANCE;
                final CodeStyleSettings currentSettings = getCurrentSettings();
                return new TabbedLanguageCodeStylePanel(language, currentSettings, settings) {
                    @Override
                    protected void initTabs(CodeStyleSettings settings) {
                        addIndentOptionsTab(settings);
                        addSpacesTab(settings);
                        addBlankLinesTab(settings);
                        addWrappingAndBracesTab(settings);
                    }
                };
            }

            @Nullable
            @Override
            public String getHelpTopic() {
                return "reference.settingsdialog.codestyle.json";
            }//TODO do we need this?
        };
    }
*/

    @Nullable
    @Override
    public String getConfigurableDisplayName() {
        return DataSonnetLanguage.INSTANCE.getDisplayName();
    }

    @Nullable
    @Override
    public CustomCodeStyleSettings createCustomSettings(CodeStyleSettings settings) {
        return new DataSonnetCodeStyleSettings(settings);
    }
}
