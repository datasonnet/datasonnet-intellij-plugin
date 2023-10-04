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

import com.intellij.util.PlatformIcons;
import com.intellij.xdebugger.frame.*;
import io.portx.datasonnet.debug.DataSonnetDebuggerSession;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Map;

public class MapOfObjectFieldDefinitionValue extends XValue {

    private final DataSonnetDebuggerSession session;
    private final Map<String, String> values;
    private final Icon icon;

    public MapOfObjectFieldDefinitionValue(DataSonnetDebuggerSession session, Map<String, String> values, Icon icon) {
        this.session = session;
        this.values = values;
        this.icon = icon;
    }

    @Override
    public void computePresentation(@NotNull XValueNode xValueNode, @NotNull XValuePlace xValuePlace) {
        xValueNode.setPresentation(icon, "", "", !values.isEmpty());
    }

    @Override
    public void computeChildren(@NotNull XCompositeNode node) {
        final XValueChildrenList list = new XValueChildrenList();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            Object nextValue = entry.getValue();
            String key = entry.getKey();
            list.add(
                key, new ObjectFieldDefinitionValue(key, session, nextValue, PlatformIcons.PROPERTY_ICON)
            );
        }
        node.addChildren(list, false);
        super.computeChildren(node);
    }
}
