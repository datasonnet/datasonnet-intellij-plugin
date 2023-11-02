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
package io.portx.datasonnet.debug.breakpoint;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.DocumentUtil;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.breakpoints.XLineBreakpointType;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import io.portx.datasonnet.debug.DataSonnetDebuggerEditorsProvider;
import io.portx.datasonnet.language.psi.DataSonnetTokenType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DataSonnetBreakpointType extends XLineBreakpointType<XBreakpointProperties<?>> {

/*
    private static final List<String> NO_BREAKPOINTS_AT = Arrays.asList(
        "routes",
        "route",
        "from",
        "routeConfiguration",
        "routeConfigurationId",
        "exception",
        "handled",
        "simple",
        "constant",
        "datasonnet",
        "groovy",
        "steps",
        "name",
        "constant",
        "uri");
*/

    protected DataSonnetBreakpointType() {
        super("datasonnet", "DataSonnet Breakpoints");
    }

    @Override
    public boolean canPutAt(@NotNull VirtualFile file, int line, @NotNull Project project) {

        //XDebuggerUtil.iterateLine - if element is anything but Comment, whitespace COMMA, R_CURLY, L_CURLY, R_BRACKET, L_BRACKET
        final Document document = FileDocumentManager.getInstance().getDocument(file);
        Ref<Boolean> res = Ref.create(false);

        XDebuggerUtil.getInstance().iterateLine(project, document, line, element -> {
            if (!(element instanceof PsiComment) && !(element instanceof PsiWhiteSpace)) {
                if (element instanceof LeafPsiElement) {
                    LeafPsiElement leafPsiElement = (LeafPsiElement) element;
                    IElementType type = leafPsiElement.getElementType();
                    String name = type.getDebugName();
                    if (!name.equals("COMMA") &&
                        !name.equals("R_CURLY") &&
                        !name.equals("R_BRACKET") &&
                        !name.equals("L_CURLY") &&
                        !name.equals("L_BRACKET") &&
                        !name.equals("SEMICOLON")
                    ) {
                        res.set(true);
                        return false; //We found element that can have breakpoint, stop processing
                    }
                }
            }
            return true;
        });

        return res.get();
    }

    @Override
    public XDebuggerEditorsProvider getEditorsProvider(@NotNull XLineBreakpoint<XBreakpointProperties<?>> breakpoint,
                                                       @NotNull Project project) {
        final XSourcePosition position = breakpoint.getSourcePosition();
        if (position == null) {
            return null;
        }

        final PsiFile file = PsiManager.getInstance(project).findFile(position.getFile());
        if (file == null) {
            return null;
        }

        return new DataSonnetDebuggerEditorsProvider();
    }

    @Nullable
    @Override
    public XBreakpointProperties<?> createBreakpointProperties(@NotNull VirtualFile virtualFile, int line) {
        return new DataSonnetBreakpointProperties(virtualFile.getFileType());
    }

    static class DataSonnetBreakpointProperties extends XBreakpointProperties<DataSonnetBreakpointProperties> {
        private FileType myFileType;

        DataSonnetBreakpointProperties(FileType fileType) {
            myFileType = fileType;
        }

        @Override
        public @Nullable DataSonnetBreakpointProperties getState() {
            return this;
        }

        @Override
        public void loadState(@NotNull DataSonnetBreakpointProperties state) {
            myFileType = state.myFileType;
        }

        public FileType getFileType() {
            return myFileType;
        }
    }
}
