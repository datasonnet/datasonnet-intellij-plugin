package com.modusbox.portx.datasonnet.language;

import com.intellij.openapi.fileTypes.*;
import org.jetbrains.annotations.NotNull;

public class DataSonnetFileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(
                DataSonnetFileType.INSTANCE,
                new FileNameMatcher() {
                    @Override
                    public boolean accept(@NotNull String fileName) {
                        return fileName.endsWith(".ds");
                    }

                    @NotNull
                    @Override
                    public String getPresentableString() {
                        return ".ds";
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
                        return fileName.endsWith(".ds.TEMPLATE");
                    }

                    @NotNull
                    @Override
                    public String getPresentableString() {
                        return ".ds.TEMPLATE";
                    }
                }
        );
    }
}