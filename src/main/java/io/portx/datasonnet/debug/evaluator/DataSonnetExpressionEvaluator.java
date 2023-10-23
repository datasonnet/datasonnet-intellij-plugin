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
package io.portx.datasonnet.debug.evaluator;

import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import io.portx.datasonnet.debug.DataSonnetDebuggerSession;
import io.portx.datasonnet.debug.stack.DataSonnetValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DataSonnetExpressionEvaluator extends XDebuggerEvaluator {

    private final DataSonnetDebuggerSession session;

    public DataSonnetExpressionEvaluator(@NotNull DataSonnetDebuggerSession session) {
        this.session = session;
    }

    @Override
    public void evaluate(@NotNull String expression, @NotNull XEvaluationCallback xEvaluationCallback, @Nullable XSourcePosition expressionPosition) {
        Object result = session.evaluateExpression(expression);
        xEvaluationCallback.evaluated(new DataSonnetValue(session, result));
    }
}
