package io.portx.datasonnet.debug;

import com.datasonnet.debugger.StoppedProgramContext;
import com.datasonnet.debugger.da.DataSonnetDebugListener;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.util.ArrayUtil;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import com.intellij.xdebugger.frame.XSuspendContext;
import com.intellij.xdebugger.impl.ui.tree.XDebuggerTreeState;
import io.portx.datasonnet.debug.breakpoint.DataSonnetBreakpointHandler;
import io.portx.datasonnet.debug.stack.DataSonnetStackFrame;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DataSonnetDebugProcess extends XDebugProcess {
    private final DataSonnetDebuggerSession dataSonnetDebuggerSession;
    private final DataSonnetDebuggerEditorsProvider dataSonnetDebuggerEditorsProvider;
    private final DataSonnetBreakpointHandler dataSonnetBreakpointHandler;

    /**
     * @param session pass {@code session} parameter of {@link XDebugProcessStarter#start} method to this constructor
     */
    public DataSonnetDebugProcess(@NotNull XDebugSession session, @NotNull DataSonnetDebuggerSession dataSonnetDebuggerSession) {
        super(session);
        this.dataSonnetDebuggerEditorsProvider = new DataSonnetDebuggerEditorsProvider();
        this.dataSonnetDebuggerSession = dataSonnetDebuggerSession;
        this.dataSonnetBreakpointHandler = new DataSonnetBreakpointHandler(dataSonnetDebuggerSession);

        dataSonnetDebuggerSession.addDebuggerListener(new DataSonnetDebugListener() {
            @Override
            public void stopped(StoppedProgramContext stoppedProgramContext) {
                DataSonnetStackFrame frame = new DataSonnetStackFrame(dataSonnetDebuggerSession, stoppedProgramContext);
                getSession().positionReached(new DataSonnetSuspendContext(frame));
            }
        });

        session.addSessionListener(new XDebugSessionListener() {
            private XDebuggerTreeState state = null;
            @Override
            public void sessionPaused() {
                XDebugSessionListener.super.sessionPaused();
            }

            @Override
            public void sessionResumed() {
                XDebugSessionListener.super.sessionResumed();
            }
        });
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
