package io.portx.datasonnet.debug;

import com.datasonnet.debugger.DataSonnetDebugger;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.AbstractDebuggerSession;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import io.portx.datasonnet.debug.runner.DataSonnetProcessHandler;
import org.jetbrains.annotations.NotNull;

public class DataSonnetDebuggerSession  implements AbstractDebuggerSession {
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
        DataSonnetDebugger.getDebugger().addBreakpoint(xBreakpoint.getLine());
    }

    public void removeBreakpoint(XLineBreakpoint<XBreakpointProperties<?>> xBreakpoint) {
        DataSonnetDebugger.getDebugger().removeBreakpoint(xBreakpoint.getLine());
    }
    public void resume() {

    }
    public void stepInto(XSourcePosition position) {

    }

    public void stepOver(XSourcePosition position) {

    }

    public void stepOut(XSourcePosition position) {

    }
    public void runToPosition(XSourcePosition fromPosition, XSourcePosition toPosition) {

    }

    public DataSonnetProcessHandler getDataSonnetProcessHandler() {
        return dataSonnetProcessHandler;
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
