package io.portx.datasonnet.debug.stack;

import com.datasonnet.debugger.StoppedProgramContext;
import com.intellij.debugger.engine.JavaStackFrame;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XValueChildrenList;
import io.portx.datasonnet.debug.DataSonnetDebuggerSession;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

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
        VirtualFile mappingFile = session.getDataSonnetProcessHandler().getDataSonnetEngine().getMappingFile();
        return XDebuggerUtil.getInstance().createPosition(mappingFile, context.getSourcePos().getLine());
    }

    @Override
    public void computeChildren(@NotNull XCompositeNode node) {
        final XValueChildrenList children = new XValueChildrenList();

        children.add("Named Variables", new MapOfObjectFieldDefinitionValue(this.session, context.getNamedVariables(), AllIcons.Debugger.Value));

/*
        children.add("ExchangeId", new ObjectFieldDefinitionValue(this.session, this.camelMessageInfo.exchangeIdAsValue(), AllIcons.Debugger.Value));
        children.add("Body", new ObjectFieldDefinitionValue(CamelDebuggerTarget.BODY, null, this.session, this.camelMessageInfo.getBody(), AllIcons.Debugger.Value));
        children.add("Headers", new MapOfObjectFieldDefinitionValue(CamelDebuggerTarget.MESSAGE_HEADER, this.session, this.camelMessageInfo.getHeaders(), AllIcons.Debugger.Value));
        final var properties = this.camelMessageInfo.getProperties();
        if (properties == null) {
            children.add("WARNING: ", JavaStackFrame.createMessageNode("Exchange Properties in Debugger are only available in Camel version 3.15 or later", AllIcons.Nodes.WarningMark));
        } else {
            children.add("Exchange Properties", new MapOfObjectFieldDefinitionValue(CamelDebuggerTarget.EXCHANGE_PROPERTY, this.session, properties, AllIcons.Debugger.Value));
        }
*/

        node.addChildren(children, true);
    }
}
