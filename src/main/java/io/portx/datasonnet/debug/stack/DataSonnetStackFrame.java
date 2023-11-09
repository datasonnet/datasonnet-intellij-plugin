package io.portx.datasonnet.debug.stack;

import com.datasonnet.debugger.StoppedProgramContext;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XValueChildrenList;
import io.portx.datasonnet.debug.DataSonnetDebuggerSession;
import io.portx.datasonnet.debug.evaluator.DataSonnetExpressionEvaluator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DataSonnetStackFrame extends XStackFrame {
    private final StoppedProgramContext context;
    private final DataSonnetDebuggerSession session;

    public DataSonnetStackFrame(@NotNull DataSonnetDebuggerSession session, StoppedProgramContext context) {
        this.session = session;
        this.context = context;
    }

    @Nullable
    @Override
    public XSourcePosition getSourcePosition() {
        try {
            VirtualFile mappingFile = session.getDataSonnetProcessHandler().getDataSonnetEngine().getMappingFile();
            return XDebuggerUtil.getInstance().createPosition(mappingFile, context.getSourcePos().getLine(), context.getSourcePos().getCaretPosInLine());
        } catch (Exception e) { //Process may be terminated at this point
            return null;
        }
    }

    @Override
    public void computeChildren(@NotNull XCompositeNode node) {
        final XValueChildrenList children = new XValueChildrenList();

        context.getNamedVariables().forEach((k, v) -> children.add(k, new DataSonnetValue(this.session, v)));
        //children.add("Named Variables", new DataSonnetValue(this.session, context.getNamedVariables()));
        //TODO Local variables and fields?

        node.addChildren(children, true);
    }

    @Nullable
    @Override
    public XDebuggerEvaluator getEvaluator() {
        return new DataSonnetExpressionEvaluator(session);
    }

    //FIXME - this is temporary, see the conversation here https://intellij-support.jetbrains.com/hc/en-us/community/posts/14378171866258-How-can-I-keep-the-watches-tree-expanded-
    @Override
    public Object getEqualityObject() {
        return "NOT_NULL";
    }
}
