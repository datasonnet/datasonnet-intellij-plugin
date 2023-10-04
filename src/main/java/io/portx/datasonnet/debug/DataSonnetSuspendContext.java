package io.portx.datasonnet.debug;

import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XSuspendContext;
import io.portx.datasonnet.debug.stack.DataSonnetExecutionStack;

public class DataSonnetSuspendContext extends XSuspendContext {
    private final DataSonnetExecutionStack dataSonnetExecutionStack;

    public DataSonnetSuspendContext(XStackFrame... frame) {
        dataSonnetExecutionStack = new DataSonnetExecutionStack("DataSonnet Execution", frame);
    }

    @Override
    public XExecutionStack getActiveExecutionStack() {
        return dataSonnetExecutionStack;
    }
}
