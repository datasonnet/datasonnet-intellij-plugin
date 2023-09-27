package io.portx.datasonnet.debug.runner;

import com.intellij.debugger.impl.GenericDebuggerRunner;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.RemoteConnection;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunContentBuilder;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugProcessStarter;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import io.portx.datasonnet.debug.DataSonnetDebugProcess;
import io.portx.datasonnet.debug.DataSonnetDebuggerSession;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;

public class DataSonnetProgramRunner extends GenericDebuggerRunner {
    private static final Logger LOG = Logger.getInstance(DataSonnetProgramRunner.class);

    @NonNls
    private static final String ID = "DataSonnetDebuggerRunner";

    @NotNull
    @Override
    public String getRunnerId() {
        return ID;
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        boolean isDebug = executorId.equals(DefaultDebugExecutor.EXECUTOR_ID);
        return isDebug && profile instanceof DataSonnetRunConfiguration;
        //TODO do we need to check that mapping and scenario exist?
    }

    @Nullable
    @Override
    protected RunContentDescriptor createContentDescriptor(@NotNull RunProfileState state,
                                                           @NotNull ExecutionEnvironment env) throws ExecutionException {
        AtomicReference<ExecutionException> ex = new AtomicReference<>();
        AtomicReference<RunContentDescriptor> result = new AtomicReference<>();

        ApplicationManager.getApplication().invokeAndWait(() -> {
            try {
                XDebugProcessStarter starter = new XDebugProcessStarter() {
                    @Override
                    @NotNull
                    public XDebugProcess start(@NotNull XDebugSession session) {
                        DataSonnetRunProfileState dataSonnetRunProfileState = (DataSonnetRunProfileState) state;
                        DataSonnetDebuggerSession dataSonnetDebuggerSession = new DataSonnetDebuggerSession(env.getProject(),
                                dataSonnetRunProfileState.getDataSonnetProcessHandler());
                        return new DataSonnetDebugProcess(session, dataSonnetDebuggerSession);
                    }
                };

                XDebugSession xDebugSession = XDebuggerManager.getInstance(env.getProject()).startSession(env, starter);
                RunContentDescriptor descriptor = xDebugSession.getRunContentDescriptor();
                DataSonnetRunProfileState dsState = (DataSonnetRunProfileState)state;
                dsState.setConsoleView((ConsoleView) descriptor.getExecutionConsole());
                state.execute(env.getExecutor(), this);
                result.set(descriptor);
                //result.set(new RunContentBuilder(executionResult, env).showRunContent(descriptor));
            } catch (ExecutionException e) {
                ex.set(e);
            }
        });
        if (ex.get() != null) {
            throw ex.get();
        }
        return result.get();
    }


}
