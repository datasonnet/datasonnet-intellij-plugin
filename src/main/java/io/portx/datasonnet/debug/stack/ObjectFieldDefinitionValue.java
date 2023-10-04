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

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.pom.NonNavigatable;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.frame.XNavigatable;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.frame.XValueNode;
import com.intellij.xdebugger.frame.XValuePlace;
import com.intellij.xdebugger.impl.XSourcePositionImpl;
import io.portx.datasonnet.debug.DataSonnetDebuggerSession;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ObjectFieldDefinitionValue extends XValue {
    private final String targetName;
    private final DataSonnetDebuggerSession session;
    private final Icon icon;
    private final Object value;

    public ObjectFieldDefinitionValue(DataSonnetDebuggerSession session, Object value, Icon icon) {
        this(null, session, value, icon);
    }

    public ObjectFieldDefinitionValue(String targetName, DataSonnetDebuggerSession session, Object value, Icon icon) {
        this.targetName = targetName;
        this.session = session;
        this.value = value;
        this.icon = icon;
    }

    @Override
    public void computePresentation(@NotNull XValueNode node, @NotNull XValuePlace xValuePlace) {
        node.setPresentation(icon, "Named Variable", String.valueOf(value), false);
    }

    @Override
    public boolean canNavigateToTypeSource() {
        return false;
    }

    @Override
    public boolean canNavigateToSource() {
        return true;
    }

    @Override
    public void computeSourcePosition(@NotNull XNavigatable navigatable) {
        /* Slow operations are prohibited on EDT. Executing on pooled thread */
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            ApplicationManager.getApplication().runReadAction(() -> {
                PsiClass aClass = JavaPsiFacade.getInstance(session.getProject()).findClass(String.class.getName(), GlobalSearchScope.allScope(session.getProject()));
                if (aClass != null) {
                    navigatable.setSourcePosition(createPositionByElement(aClass));
                }
            });
        });
    }

    @Nullable
    private XSourcePosition createPositionByElement(PsiElement element) {
        if (element == null) {
            return null;
        }
        PsiFile psiFile = element.getContainingFile();
        if (psiFile == null) {
            return null;
        }
        final VirtualFile file = psiFile.getVirtualFile();
        if (file == null) {
            return null;
        }
        final SmartPsiElementPointer<PsiElement> pointer =
                SmartPointerManager.getInstance(element.getProject()).createSmartPsiElementPointer(element);

        return new XSourcePosition() {
            private volatile XSourcePosition myDelegate;

            private XSourcePosition getDelegate() {
                if (myDelegate == null) {
                    synchronized (this) {
                        if (myDelegate == null) {
                            myDelegate = ApplicationManager.getApplication().runReadAction((Computable<XSourcePosition>) () -> {
                                PsiElement elem = pointer.getElement();
                                return XSourcePositionImpl.createByOffset(pointer.getVirtualFile(), elem != null ? elem.getTextOffset() : -1);
                            });
                        }
                    }
                }
                return myDelegate;
            }

            @Override
            public int getLine() {
                return getDelegate().getLine();
            }

            @Override
            public int getOffset() {
                return getDelegate().getOffset();
            }

            @NotNull
            @Override
            public VirtualFile getFile() {
                return file;
            }

            @NotNull
            @Override
            public Navigatable createNavigatable(@NotNull Project project) {
                // no need to create delegate here, it may be expensive
                XSourcePosition delegate = myDelegate;
                if (delegate != null) {
                    return delegate.createNavigatable(project);
                }
                PsiElement elem = pointer.getElement();
                if (elem instanceof Navigatable) {
                    return (Navigatable) elem;
                }
                return NonNavigatable.INSTANCE;
            }
        };
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
