package io.portx.datasonnet.debug.runner;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;
import io.portx.datasonnet.engine.DataSonnetEngine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DataSonnetRunProfileState implements RunProfileState {
    private DataSonnetProcessHandler dataSonnetProcessHandler;
    private ConsoleView consoleView;

    private final Project project;
    public DataSonnetRunProfileState(DataSonnetRunConfiguration dataSonnetRunConfiguration) {
        this.project = dataSonnetRunConfiguration.getProject();
        DataSonnetEngine dataSonnetEngine =
                new DataSonnetEngine(
                        dataSonnetRunConfiguration.getProject(),
                    dataSonnetRunConfiguration.getScriptName(),
                    dataSonnetRunConfiguration.getMappingScenario(),
                    dataSonnetRunConfiguration.getOutputMimeType(), true);
        dataSonnetProcessHandler =
                new DataSonnetProcessHandler(this.project, dataSonnetEngine);
    }

    @Override
    public @Nullable ExecutionResult execute(Executor executor, @NotNull ProgramRunner<?> runner) throws ExecutionException {
        consoleView.attachToProcess(dataSonnetProcessHandler);
        return new DefaultExecutionResult(consoleView, dataSonnetProcessHandler);
    }

    public DataSonnetProcessHandler getDataSonnetProcessHandler() {
        return dataSonnetProcessHandler;
    }

    public void setConsoleView(ConsoleView consoleView) {
        this.consoleView = consoleView;
    }
}
