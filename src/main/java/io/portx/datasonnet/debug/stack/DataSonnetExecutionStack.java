package io.portx.datasonnet.debug.stack;

import com.intellij.icons.AllIcons;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XStackFrame;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DataSonnetExecutionStack extends XExecutionStack {
    private final List<XStackFrame> frames;

    public DataSonnetExecutionStack(@NotNull String displayName, XStackFrame... frame) {
        super(displayName, AllIcons.Debugger.ThreadSuspended);
        this.frames = Arrays.asList(frame);
    }

    @Nullable
    @Override
    public XStackFrame getTopFrame() {
        return frames.get(0);
    }

    @Override
    public void computeStackFrames(int firstFrameIndex, XStackFrameContainer container) {
        if (firstFrameIndex <= frames.size()) {
            container.addStackFrames(frames.subList(firstFrameIndex, frames.size()), true);
        } else {
            container.addStackFrames(Collections.emptyList(), true);
        }
    }
}
