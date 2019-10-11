package com.modusbox.portx.datasonnet.editor;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.PsiFile;

import javax.swing.*;

/**
 * Created by eberman on 11/15/16.
 */
public class RefreshAction extends AnAction {

    final static Logger logger = Logger.getInstance(RefreshAction.class);
    final static Icon refreshIcon = IconLoader.findIcon("/icons/refresh.png");

    DataSonnetEditor editor;

    public RefreshAction(DataSonnetEditor editor)
    {
        super("Refresh", "Force Refresh Mapping", refreshIcon);
        this.editor = editor;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        logger.debug("Refreshing!");
        final Project project = anActionEvent.getProject();
        final PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        try {
            new WriteCommandAction.Simple(project, psiFile) {
                @Override
                protected void run() throws Throwable {
                    editor.runPreview(true);
                }
            }.execute();
        } catch (Exception e) {
            logger.error(e);
        }

    }

}
