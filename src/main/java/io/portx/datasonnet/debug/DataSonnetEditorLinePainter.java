package io.portx.datasonnet.debug;

import com.datasonnet.debugger.DataSonnetDebugger;
import com.datasonnet.debugger.StoppedProgramContext;
import com.datasonnet.debugger.ValueInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.EditorLinePainter;
import com.intellij.openapi.editor.LineExtensionInfo;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.impl.inline.XDebuggerInlayUtil;
import com.intellij.xdebugger.ui.DebuggerColors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DataSonnetEditorLinePainter extends EditorLinePainter {
    final TextAttributes textAttributes = EditorColorsManager.getInstance().getGlobalScheme().getAttributes(DebuggerColors.INLINED_VALUES);
    private static final Logger LOG = Logger.getInstance(DataSonnetEditorLinePainter.class);

    @Override
    public @Nullable Collection<LineExtensionInfo> getLineExtensions(@NotNull Project project, @NotNull VirtualFile file, int lineNumber) {
        final List<LineExtensionInfo> infos = new ArrayList<>();

        StoppedProgramContext context = DataSonnetDebugger.getDebugger().getStoppedProgramContext();
        if (context == null) {
            return null;
        }

        addLineExtensionInfo(infos, context.getNamedVariables(), file, lineNumber);

        return infos;
    }

    private void addLineExtensionInfo(List<LineExtensionInfo> infos, Object value, VirtualFile file, int lineNumber) {
        if (value == null) {
            LOG.debug("Value is null, returning...");
            return;
        }

        if (value instanceof ValueInfo) {
            ValueInfo valueInfo = (ValueInfo) value;
            //No line numbers available in value info, only offset, so we need to calculate the line number
            int offset = valueInfo.getOffset();
            if (offset <= 0) {
                LOG.debug("Offset for value " + valueInfo.getKey() + " <= 0, returning...");
                return;
            }

            int line = XDebuggerUtil.getInstance().createPositionByOffset(file, valueInfo.getOffset()).getLine();
            if (line == lineNumber) {
                String valueText = valueInfo.getValue() != null ? valueInfo.getValue().toString() : "null";
                //Filter out functions
                if (!valueText.equals("FUNCTION")) {
                    LineExtensionInfo extensionInfo =
                            new LineExtensionInfo("  " + valueText + XDebuggerInlayUtil.INLINE_HINTS_DELIMETER + " ",
                                    this.textAttributes);
                    if (!infos.contains(extensionInfo)) { //Avoid duplicates because the same value can be referenced multiple times in the context
                        infos.add(extensionInfo);
                    }
                }
            }
        } else if (value instanceof List) {
            List valuesList = (List) value;
            valuesList.forEach(e -> addLineExtensionInfo(infos, e, file, lineNumber));
        } else if (value instanceof Map) {
            Map valuesMap = (Map) value;
            valuesMap.forEach((k, v) -> addLineExtensionInfo(infos, v, file, lineNumber));
        }
    }
}

