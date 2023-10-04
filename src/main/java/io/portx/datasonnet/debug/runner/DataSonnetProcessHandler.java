package io.portx.datasonnet.debug.runner;

import com.datasonnet.debugger.DataSonnetDebugger;
import com.intellij.execution.process.ProcessOutputType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.DefaultDebugProcessHandler;
import io.portx.datasonnet.engine.DataSonnetEngine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;

public class DataSonnetProcessHandler extends DefaultDebugProcessHandler {

    private DataSonnetEngine dataSonnetEngine;
    private final Project project;

    public DataSonnetProcessHandler(@NotNull Project project, DataSonnetEngine dataSonnetEngine) {
        this.dataSonnetEngine = dataSonnetEngine;
        this.project = project;
    }

    @Override
    protected void destroyProcessImpl() {
        super.destroyProcessImpl();
        dataSonnetEngine.detach();
        dataSonnetEngine = null;
    }

    @Override
    protected void detachProcessImpl() {
        super.detachProcessImpl();
        dataSonnetEngine.detach();
    }

    @Override
    public boolean detachIsDefault() {
        return false;
    }

    @Override
    public @Nullable OutputStream getProcessInput() {
        return System.out;
    }

    public DataSonnetEngine getDataSonnetEngine() {
        return dataSonnetEngine;
    }

    public DataSonnetDebugger getDebugger() {
        return dataSonnetEngine.getDebugger();
    }

    public void startProcess() {
        dataSonnetEngine.attach();
        ApplicationManager.getApplication().executeOnPooledThread(this::runDataSonnetMapping);
    }

    private void runDataSonnetMapping() {
        String result = dataSonnetEngine.runDataSonnetMapping();
        detachProcess();
        notifyTextAvailable(result, ProcessOutputType.STDOUT);
    }
}
