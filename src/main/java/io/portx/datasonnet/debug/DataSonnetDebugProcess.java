package io.portx.datasonnet.debug;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.util.ArrayUtil;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import com.intellij.xdebugger.frame.XSuspendContext;
import io.portx.datasonnet.debug.breakpoint.DataSonnetBreakpointHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DataSonnetDebugProcess extends XDebugProcess {
    private final DataSonnetDebuggerSession dataSonnetDebuggerSession;
    private final DataSonnetDebuggerEditorsProvider dataSonnetDebuggerEditorsProvider;
    private final DataSonnetBreakpointHandler dataSonnetBreakpointHandler;
    //private final DataSonnetProcessHandler dataSonnetProcessHandler;
    /**
     * @param session pass {@code session} parameter of {@link XDebugProcessStarter#start} method to this constructor
     */
    public DataSonnetDebugProcess(@NotNull XDebugSession session, @NotNull DataSonnetDebuggerSession dataSonnetDebuggerSession) {
        super(session);
        this.dataSonnetDebuggerEditorsProvider = new DataSonnetDebuggerEditorsProvider();
        this.dataSonnetDebuggerSession = dataSonnetDebuggerSession;
        this.dataSonnetBreakpointHandler = new DataSonnetBreakpointHandler(dataSonnetDebuggerSession);

        dataSonnetDebuggerSession.connect();
    }

    @Override
    public @NotNull XDebuggerEditorsProvider getEditorsProvider() {
        return dataSonnetDebuggerEditorsProvider;
    }

    @Override
    public void stop() {
        dataSonnetDebuggerSession.disconnect();
    }

    @Override
    public void resume(@Nullable XSuspendContext context) {
        dataSonnetDebuggerSession.resume();
    }

    @Override
    public void startStepOver(@Nullable XSuspendContext context) {
        dataSonnetDebuggerSession.stepOver(context.getActiveExecutionStack().getTopFrame().getSourcePosition());
    }

    @Override
    public void startStepInto(@Nullable XSuspendContext context) {
        dataSonnetDebuggerSession.stepInto(context.getActiveExecutionStack().getTopFrame().getSourcePosition());
    }

    @Override
    public void startStepOut(@Nullable XSuspendContext context) {
        dataSonnetDebuggerSession.stepOut(context.getActiveExecutionStack().getTopFrame().getSourcePosition());
    }

    @Override
    public void runToPosition(@NotNull XSourcePosition xSourcePosition, @Nullable XSuspendContext context) {
        dataSonnetDebuggerSession.runToPosition(context.getActiveExecutionStack().getTopFrame().getSourcePosition(), xSourcePosition);
    }
    @NotNull
    @Override
    public XBreakpointHandler<?>[] getBreakpointHandlers() {
        final XBreakpointHandler<?>[] breakpointHandlers = super.getBreakpointHandlers();
        return ArrayUtil.append(breakpointHandlers, dataSonnetBreakpointHandler);
    }

    @Override
    @Nullable
    protected ProcessHandler doGetProcessHandler() {
        return dataSonnetDebuggerSession.getDataSonnetProcessHandler();
    }
}