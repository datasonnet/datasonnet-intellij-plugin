/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.portx.datasonnet.debug.stack;

import com.datasonnet.debugger.ValueInfo;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.PlatformIcons;
import com.intellij.util.ThreeState;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.frame.*;
import io.portx.datasonnet.debug.DataSonnetDebuggerSession;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class DataSonnetValue extends XValue {
    private final DataSonnetDebuggerSession session;
    private final Object value;

    public DataSonnetValue(DataSonnetDebuggerSession session, Object value) {
        this.session = session;
        this.value = value;
    }

    @Override
    public void computePresentation(@NotNull XValueNode node, @NotNull XValuePlace xValuePlace) {
        if (value instanceof Map) {
            node.setPresentation(AllIcons.Json.Object, "Object", "", true);
        } else if (value instanceof List) {
            node.setPresentation(AllIcons.Json.Array, "Array", "", true);
        } else if (value instanceof ValueInfo) {
            Object infoObject = ((ValueInfo) value).getValue();
            if (infoObject instanceof Map) {
                node.setPresentation(AllIcons.Json.Object, "Object", "", true);
            } else if (infoObject instanceof List) {
                node.setPresentation(AllIcons.Json.Array, "Array", "", true);
            } else if (infoObject instanceof Throwable) {
                node.setPresentation(AllIcons.General.Error, "Error", "", true);
            } else {
                node.setPresentation(PlatformIcons.VARIABLE_ICON, "Value", infoObject != null ? infoObject.toString() : "null", false);
            }
        } else if (value instanceof Throwable) {
            node.setPresentation(AllIcons.General.Error, "Error", "", true);
        } else {
            node.setPresentation(PlatformIcons.VARIABLE_ICON, "Value", value != null ? value.toString() : "null", false);
        }
    }

    @Override
    public void computeChildren(@NotNull XCompositeNode node) {
        final XValueChildrenList list = new XValueChildrenList();
        if (value instanceof Map) {
            for (Object entry : ((Map) value).entrySet()) {
                Object nextValue = ((Map.Entry) entry).getValue();
                String key = ((Map.Entry) entry).getKey().toString();
                list.add(key, new DataSonnetValue(session, nextValue));
            }
        } else if (value instanceof List) {
            for (Object nextValue : (List) value) {
                list.add("", new DataSonnetValue(session, nextValue));
            }
        } else if (value instanceof ValueInfo info) {
            Object valueObj = info.getValue();
            if (valueObj instanceof Map) {
                for (Object entry : ((Map) valueObj).entrySet()) {
                    Object nextValue = ((Map.Entry) entry).getValue();
                    String key = ((Map.Entry) entry).getKey().toString();
                    list.add(key, new DataSonnetValue(session, nextValue));
                }
            } else if (valueObj instanceof List) {
                for (Object nextValue : (List) valueObj) {
                    list.add("", new DataSonnetValue(session, nextValue));
                }
            }
        } else if (value instanceof Throwable) {
            //TODO HOW?
        }

        node.addChildren(list, false);
        super.computeChildren(node);
    }


    @Override
    public boolean canNavigateToTypeSource() {
        return false;
    }

    @Override
    public boolean canNavigateToSource() {
        return value instanceof ValueInfo && ((ValueInfo) value).getOffset() > 0;
    }

    @Override
    public void computeSourcePosition(@NotNull XNavigatable navigatable) {
        /* Slow operations are prohibited on EDT. Executing on pooled thread */
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            if (value instanceof ValueInfo valueInfo) {
                if (valueInfo.getOffset() != 0) {
                    VirtualFile mappingFile = session.getDataSonnetProcessHandler().getDataSonnetEngine().getMappingFile();
                    XSourcePosition valuePosition = XDebuggerUtil.getInstance().createPositionByOffset(mappingFile, valueInfo.getOffset());
                    navigatable.setSourcePosition(valuePosition);
                }
            }
        });
    }

    @Override
    @NotNull
    public ThreeState computeInlineDebuggerData(@NotNull XInlineDebuggerDataCallback callback) {
        return ThreeState.YES;
    }

/*    @Override
    public @Nullable XValueModifier getModifier() {

        return new XValueModifier() {
            @Override
            public void setValue(@NotNull XExpression expression, @NotNull XModificationCallback callback) {
                String type = fieldDefinition.getType();
                if (StringUtils.isEmpty(type)) {
                    type = String.class.getName();
                }
                session.getCamelDebugProcess().setValue(
                    target, targetName, StringUtil.unquoteString(expression.getExpression()),
                    CamelLanguages.SIMPLE_LANGUAGE.getID(), type, null, null
                );
                callback.valueModified();
            }

            @Override
            public @Nullable String getInitialValueEditorText() {
                if (StringUtils.isEmpty(fieldDefinition.getType())) {
                    return "\"\"";
                }
                return String.format("\"%s\"", fieldDefinition.getValue());
            }
        };
    }*/
}
