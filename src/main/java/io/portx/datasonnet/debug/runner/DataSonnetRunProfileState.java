package io.portx.datasonnet.debug.runner;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import com.datasonnet.Mapper;
import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import io.portx.datasonnet.engine.DataSonnetEngine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

public class DataSonnetRunProfileState implements RunProfileState {
    private final DataSonnetProcessHandler dataSonnetProcessHandler;
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

        Appender appender = new AppenderBase<ILoggingEvent>() {
            @Override
            protected void append(ILoggingEvent event) {
                consoleView.print(event.getFormattedMessage() + "\n", ConsoleViewContentType.LOG_DEBUG_OUTPUT);
            }

            @Override
            public String getName() {
                return "DEBUG_CONSOLE";
            }
        };
        appender.start();

        Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("DS_TRACE");
        logger.setLevel(Level.ALL);
        logger.setAdditive(false);
        logger.addAppender(appender);
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
