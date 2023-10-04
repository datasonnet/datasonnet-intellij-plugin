package io.portx.datasonnet.debug;

import com.datasonnet.debugger.DataSonnetDebugger;
import com.datasonnet.debugger.da.DataSonnetDebugListener;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.AbstractDebuggerSession;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import io.portx.datasonnet.debug.runner.DataSonnetProcessHandler;
import org.jetbrains.annotations.NotNull;

public class DataSonnetDebuggerSession implements AbstractDebuggerSession {
    private DataSonnetProcessHandler dataSonnetProcessHandler;
    private final Project project;

    public DataSonnetDebuggerSession(@NotNull Project project, @NotNull DataSonnetProcessHandler dataSonnetProcessHandler) {
        this.dataSonnetProcessHandler = dataSonnetProcessHandler;
        this.project = project;
    }
    public void connect() {
        dataSonnetProcessHandler.startProcess();
    }

    public void disconnect() {
        dataSonnetProcessHandler.detachProcess();
    }

    @Override
    public boolean isStopped() {
        return dataSonnetProcessHandler.isProcessTerminated();
    }

    @Override
    public boolean isPaused() {
        return false;
    }

    public void addBreakpoint(XLineBreakpoint<XBreakpointProperties<?>> xBreakpoint) {
        dataSonnetProcessHandler.getDebugger().addBreakpoint(xBreakpoint.getLine());
    }

    public void removeBreakpoint(XLineBreakpoint<XBreakpointProperties<?>> xBreakpoint) {
        dataSonnetProcessHandler.getDebugger().removeBreakpoint(xBreakpoint.getLine());
    }
    public void resume() {
        dataSonnetProcessHandler.getDebugger().resume();
    }
    public void stepInto(XSourcePosition position) {

    }

    public void stepOver(XSourcePosition position) {
        dataSonnetProcessHandler.getDebugger().addBreakpoint(position.getLine() + 1, true);
        resume();
    }

    public void stepOut(XSourcePosition position) {

    }
    public void runToPosition(XSourcePosition fromPosition, XSourcePosition toPosition) {

    }

    public DataSonnetProcessHandler getDataSonnetProcessHandler() {
        return dataSonnetProcessHandler;
    }

    public void addDebuggerListener(DataSonnetDebugListener listener) {
        dataSonnetProcessHandler.getDebugger().setDebuggerAdapter(listener);
    }

    public Project getProject() {
        return project;
    }

    /*    private void runDataSonnetProcess() {
        final TextConsoleBuilder consoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(project);
        final ConsoleView console = consoleBuilder.getConsole();
        console.print("TEST SYSOUT\n", ConsoleViewContentType.SYSTEM_OUTPUT);
        console.print("TEST SYSERR\n", ConsoleViewContentType.ERROR_OUTPUT);
        console.print("TEST NORMAL", ConsoleViewContentType.NORMAL_OUTPUT);

//        dataSonnetProcess = new DataSonnetProcess(project, mapping, scenario, outputMimeType, true);
//        dataSonnetProcess.run();
    }*/
}
