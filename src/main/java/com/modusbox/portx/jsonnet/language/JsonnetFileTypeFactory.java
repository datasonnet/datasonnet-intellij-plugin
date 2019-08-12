package com.modusbox.portx.jsonnet.language;

import com.intellij.openapi.fileTypes.*;
import org.jetbrains.annotations.NotNull;

public class JsonnetFileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(
                JsonnetFileType.INSTANCE,
                new FileNameMatcher() {
                    @Override
                    public boolean accept(@NotNull String fileName) {
                        return fileName.endsWith(".jsonnet");
                    }

                    @NotNull
                    @Override
                    public String getPresentableString() {
                        return ".jsonnet";
                    }
                },
                new FileNameMatcher() {
                    @Override
                    public boolean accept(@NotNull String fileName) {
                        return fileName.endsWith(".libsonnet");
                    }

                    @NotNull
                    @Override
                    public String getPresentableString() {
                        return ".libsonnet";
                    }
                },
                new FileNameMatcher() {

                    @Override
                    public boolean accept(@NotNull String fileName) {
                        return fileName.endsWith(".jsonnet.TEMPLATE");
                    }

                    @NotNull
                    @Override
                    public String getPresentableString() {
                        return ".jsonnet.TEMPLATE";
                    }
                }
        );
    }
}