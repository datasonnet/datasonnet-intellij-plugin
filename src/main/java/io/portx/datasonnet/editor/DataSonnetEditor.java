package io.portx.datasonnet.editor;

import com.datasonnet.document.DefaultDocument;
import com.datasonnet.document.MediaType;
import com.datasonnet.document.MediaTypes;
import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.icons.AllIcons;
import com.intellij.json.JsonLanguage;
import com.intellij.lang.Language;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.navigation.ItemPresentation;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.impl.ActionManagerImpl;
import com.intellij.openapi.actionSystem.impl.MenuItemPresentationFactory;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.fileEditor.impl.text.TextEditorImpl;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ui.configuration.ProjectSettingsService;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.ParameterizedCachedValue;
import com.intellij.ui.JBColor;
import com.intellij.ui.JBTabsPaneImpl;
import com.intellij.ui.content.TabbedPaneContentUI;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.util.Alarm;
import io.portx.datasonnet.engine.DataSonnetEngine;
import io.portx.datasonnet.engine.Scenario;
import io.portx.datasonnet.engine.ScenarioManager;
import io.portx.datasonnet.language.DataSonnetFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.java.JavaResourceRootType;
import org.jetbrains.yaml.YAMLLanguage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.datasonnet.StringDocument;

/**
 * Created by eberman on 11/3/16.
 */
public class DataSonnetEditor implements FileEditor {

    private Project project;

    private final TextEditorImpl textEditor;

    private final PsiFile psiFile;

    private final Map<String, Editor> editors = new HashMap<String, Editor>();
    private final Map<String, Editor> outputEditors = new HashMap<String, Editor>();

    private final JBTabsPaneImpl outputTabs;
    private final JBTabsPaneImpl inputTabs;

    private final Map<String, TabInfo> previewTabInfoMap = new HashMap<>();

    private final DataSonnetEditorUI gui;

    final static Logger logger = Logger.getInstance(DataSonnetEditor.class);

    private final static long PREVIEW_DELAY = 500;

    Alarm myDocumentAlarm = new Alarm(Alarm.ThreadToUse.SWING_THREAD, this);
    DocumentListener refreshPreview;

    private boolean autoSync = false;

    private MediaType outputMimeType = MediaTypes.APPLICATION_JSON;

    private static final Key<ParameterizedCachedValue<Map<String, String>, VirtualFile>> DS_LIBRARIES_KEY = Key.create("DS_LIBRARIES");

    private DefaultActionGroup actionGroup;

    private List<Class<?>> libsClasses = null;

    public DataSonnetEditor(@NotNull Project project, @NotNull VirtualFile virtualFile, final TextEditorImpl psiAwareEditor) {
        this.project = project;
        this.textEditor = psiAwareEditor;

        gui = new DataSonnetEditorUI(textEditor);

        inputTabs = new JBTabsPaneImpl(project, SwingConstants.TOP, this);
        inputTabs.getTabs().getPresentation().setSideComponentVertical(true);

        gui.getSourcePanel().add(inputTabs.getComponent(), BorderLayout.CENTER);

        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new AnAction("Add new scenario", "Adds a new scenario for the current mapping", IconLoader.findIcon("/icons/addScenario.svg", DataSonnetEditor.class)) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                //Check if there is a test resources folder
                Module module = ApplicationManager.getApplication().runReadAction((Computable<Module>) () -> ModuleUtilCore.findModuleForFile(virtualFile, project));
                List<VirtualFile> testRoots = module != null ? ModuleRootManager.getInstance(module).getSourceRoots(JavaResourceRootType.TEST_RESOURCE) : null;
                if (testRoots == null || testRoots.isEmpty()) {
                    Notification notification = new Notification("DataSonnet",
                            "No Test Resources folder found!",
                            "Please mark at least one directory as <strong>Test Resources</strong> folder.",
                            NotificationType.WARNING);

                    notification.addAction(new NotificationAction("Open project settings") {
                        @Override
                        public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                            notification.expire();
                            ProjectSettingsService.getInstance(project).openModuleSettings(module);
                        }
                    });

                    Notifications.Bus.notify(notification);
                    return;
                }

                final ScenarioManager manager = ScenarioManager.getInstance(project);
                manager.setModule(module);
                AddScenarioDialog dialog = new AddScenarioDialog(project, manager, psiFile, (scenario) -> {
                    manager.setCurrentScenario(psiFile.getVirtualFile().getCanonicalPath(), scenario);
                    loadScenario(scenario);
                    if (gui.getInputsSplitter().getProportion() == 0 || !gui.getInputsSplitter().isEnabled()) {
                        gui.getInputsSplitter().setProportion(0.3f);
                        gui.getInputsSplitter().setEnabled(true);
                    }
                });
                dialog.show();
            }

            @Override
            public void update(AnActionEvent e) {

            }

            @Override
            public @NotNull ActionUpdateThread getActionUpdateThread() {
                return ActionUpdateThread.BGT;
            }
        });
        actionGroup.add(new SelectScenarioAction());

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("Scenarios", actionGroup, false);
        toolbar.setTargetComponent(inputTabs.getComponent());
        toolbar.getComponent().setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, JBColor.border()));

        gui.getRootPanel().add(toolbar.getComponent(), BorderLayout.WEST);

        outputTabs = new JBTabsPaneImpl(project, SwingConstants.TOP, this);
        outputTabs.getTabs().getPresentation().setSideComponentVertical(true);
        gui.getOutputPanel().add(outputTabs.getComponent(), BorderLayout.CENTER);
        gui.getOutputPanel().setSize(1000, 1000);

        refreshPreview = new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                if (myDocumentAlarm.isDisposed())
                    return;

                myDocumentAlarm.cancelAllRequests();
                myDocumentAlarm.addRequest(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            WriteCommandAction.writeCommandAction(project, psiFile).run(() -> {
                                runPreview(false);
                            });
                        } catch (Exception e) {
                            logger.error(e);
                        }

                    }
                }, PREVIEW_DELAY);
            }
        };

        try {
            this.textEditor.getEditor().getDocument().addDocumentListener(refreshPreview);
        } catch (Throwable e) {
        }

        psiFile = PsiManager.getInstance(project).findFile(virtualFile);

        if (psiFile != null && psiFile.getFileType() == DataSonnetFileType.INSTANCE) {
            Module module = ApplicationManager.getApplication().runReadAction((Computable<Module>) () -> ModuleUtilCore.findModuleForFile(virtualFile, project));
            ScenarioManager manager = ScenarioManager.getInstance(project);
            manager.setModule(module);
            final Application app = ApplicationManager.getApplication();
            Runnable action = new Runnable() {
                @Override
                public void run() {
                    app.runWriteAction(new Runnable() {
                        @Override
                        public void run() {
                            java.util.List<Scenario> scenarios = manager.getScenariosFor(psiFile);
                            if (!scenarios.isEmpty()) {
                                Scenario first = scenarios.get(0);
                                manager.setCurrentScenario(psiFile.getVirtualFile().getCanonicalPath(), first);
                                loadScenario(first);
                                gui.getInputsSplitter().setEnabled(true);
                                gui.getInputsSplitter().setProportion(0.3f);
                            } else {
                                gui.getInputsSplitter().setEnabled(false);
                                gui.getInputsSplitter().setProportion(0);
                            }
                        }
                    });
                }
            };
            if (app.isDispatchThread()) {
                action.run();
            } else {
                app.invokeAndWait(action, ModalityState.current());
            }

            //scanLibraries();
            createOutputTab();
            ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                @Override
                public void run() {
                    runPreview(true);
                }
            });
        }
    }

    @NotNull
    @Override
    public VirtualFile getFile() {
        return psiFile.getVirtualFile();
    }

    @NotNull
    @Override
    public JComponent getComponent() {
        return gui.getRootPanel();
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return textEditor.getPreferredFocusedComponent();
    }

    @NotNull
    @Override
    public String getName() {
        return "Datasonnet Editor";
    }

    @Override
    public void setState(@NotNull FileEditorState fileEditorState) {
        textEditor.setState(fileEditorState);
    }

    @Override
    public FileEditorState getState(@NotNull FileEditorStateLevel level) {
        return textEditor.getState(level);
    }

    @Override
    public boolean isModified() {
        return textEditor.isModified();
    }

    @Override
    public boolean isValid() {
        return textEditor.isValid();
    }

    @Override
    public void selectNotify() {

    }

    @Override
    public void deselectNotify() {

    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {

    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {

    }

    @Nullable
    @Override
    public BackgroundEditorHighlighter getBackgroundHighlighter() {
        return null;
    }

    @Nullable
    @Override
    public FileEditorLocation getCurrentLocation() {
        return null;
    }

    @Override
    public void dispose() {
        for (Editor editor : editors.values()) {
            if (!editor.isDisposed()) {
                EditorFactory.getInstance().releaseEditor(editor);
            }
        }
        EditorFactory.getInstance().releaseEditor(textEditor.getEditor());
    }

    @Nullable
    @Override
    public <T> T getUserData(@NotNull Key<T> key) {
        return null;
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T t) {

    }

    private com.datasonnet.document.Document runPreviewBuiltIn() {
        ScenarioManager manager = ScenarioManager.getInstance(project);
        Scenario currentScenario = manager.getCurrentScenario(getPsiFile().getVirtualFile().getCanonicalPath());

        if (currentScenario == null)
            return new DefaultDocument<>("ERROR: No mapping scenarios available!", MediaTypes.TEXT_PLAIN);

        DataSonnetEngine dataSonnetEngine =
                new DataSonnetEngine(project, this.textEditor.getFile(), currentScenario, outputMimeType);
        return dataSonnetEngine.runDataSonnetMapping();
    }

    protected void runPreview(boolean forceRefresh) {
        if (!isAutoSync() && !forceRefresh)
            return;

        //Commit all data
        WriteCommandAction.writeCommandAction(project, psiFile).run(() -> {
            for (Editor nextEditor : editors.values()) {
                try {
                    Document doc = nextEditor.getDocument();
                    PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
                    if (psiDocumentManager.isUncommited(doc) && project.isInitialized()) {
                        psiDocumentManager.commitDocument(doc);
                        //FileDocumentManager.getInstance().saveDocument(doc);
                        FileDocumentManager.getInstance().saveAllDocuments();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        this.textEditor.getEditor().getMarkupModel().removeAllHighlighters();

        com.datasonnet.document.Document doc = runPreviewBuiltIn();
        String preview = doc.getContent().toString();
        String contentType = doc.getMediaType().toString();

        if (preview != null) {
            if (preview.startsWith("Problem")) {
                Document document = this.textEditor.getEditor().getDocument();

                try {
                    // Just to be sure we'll split on all possible line endings.
                    String errLine = preview.split("\n|\r|\r\n")[1];
                    String line = errLine.substring(errLine.indexOf("line"), errLine.indexOf("column"));
                    String column = errLine.substring(errLine.indexOf("column"));

                    line = line.replaceAll("[^\\d.]", "");
                    column = column.replaceAll("[^\\d.]", "");

                    int lineNumber = Integer.parseInt(line) - 1;
                    int columnNumber = Integer.parseInt(column);

                    int startOffset = document.getLineStartOffset(lineNumber) + columnNumber;
                    int endOffset = document.getLineEndOffset(lineNumber);

                    TextAttributes err = new TextAttributes(null, null, JBColor.RED, EffectType.WAVE_UNDERSCORE, Font.PLAIN);

                    if (startOffset < endOffset) {
                        RangeHighlighter rangeHighlighter = this.textEditor.getEditor().getMarkupModel().addRangeHighlighter(startOffset, endOffset, 0, err, HighlighterTargetArea.EXACT_RANGE);
                        rangeHighlighter.setErrorStripeMarkColor(JBColor.RED);
                    }
                } catch (Exception e) {

                }
            }

            try {
                WriteCommandAction.writeCommandAction(project, psiFile).run(() -> {
                    updateOutputTab(preview, contentType);
                });
            } catch (Exception e) {
                logger.error(e);
            }

        }
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public boolean isAutoSync() {
        return autoSync;
    }

    public void setAutoSync(boolean autoSync) {
        this.autoSync = autoSync;
    }

    private void createOutputTab() {
        updateOutputTab("", "application/json");
    }

    private void updateOutputTab(String contents, String mimeType) {

        Icon icon;
        Language language;

        if ("application/json".equals(mimeType)) {
            icon = AllIcons.FileTypes.Json;
            language = JsonLanguage.INSTANCE;
        } else if ("application/xml".equals(mimeType)) {
            icon = AllIcons.FileTypes.Xml;
            language = XMLLanguage.INSTANCE;
        } else if ("application/x-yaml".equals(mimeType)) {
            icon = AllIcons.FileTypes.Yaml;
            language = YAMLLanguage.INSTANCE;
        } else {
            icon = AllIcons.FileTypes.Text;
            language = PlainTextLanguage.INSTANCE;
        }

        final String title = "Preview";

        Editor editor = outputEditors.get(mimeType);
        PsiFile f = PsiFileFactory.getInstance(getProject()).createFileFromText(language, contents);
        if (editor == null) {
            editor = EditorFactory.getInstance().createEditor(f.getViewProvider().getDocument(), getProject(), language.getAssociatedFileType(), true);
            outputEditors.put(mimeType, editor);
        }
        final Editor currentEditor = editor;

        final Application app = ApplicationManager.getApplication();
        Runnable action = new Runnable() {
            @Override
            public void run() {
                app.runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                        // On Windows, the line separators are \r\n, but the editor expects \n.
                        currentEditor.getDocument().setText(StringUtil.convertLineSeparators(contents));
                    }
                });
            }
        };
        if (app.isDispatchThread()) {
            action.run();
        } else {
            app.invokeAndWait(action, ModalityState.current());
        }

        FileType newType = language.getAssociatedFileType();
        ((EditorEx) editor).setHighlighter(EditorHighlighterFactory.getInstance().createEditorHighlighter(project, newType));
        ((EditorEx) editor).setFile(f.getVirtualFile());

        if (project.isInitialized()) {
//            Logger.getInstance(DataSonnetEditor.class).error("IS INITIALIZED " + project.isInitialized() + " ; IS DEFAULT " + project.isDefault());
            Document doc = editor.getDocument();
            PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
            if (psiDocumentManager.isUncommited(doc)) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    try {
                        psiDocumentManager.commitDocument(doc);
                        WriteCommandAction.runWriteCommandAction(project, () -> {
                            PsiFile previewPsiFile = psiDocumentManager.getPsiFile(doc);
                            CodeStyleManager.getInstance(project).reformat(previewPsiFile);
                        });
                    } catch (Throwable e) {
                        logger.error(e);
                    }
                });
            }

            if (!psiDocumentManager.isUncommited(doc)) {
                PsiFile previewPsiFile = psiDocumentManager.getPsiFile(doc);
                CodeStyleManager.getInstance(project).reformat(previewPsiFile);
            }
        }

        outputTabs.getTabs().getPresentation().setSideComponentVertical(true);

        TabInfo previewTabInfo = previewTabInfoMap.get(mimeType);
        if (previewTabInfo == null) {
            previewTabInfo = new TabInfo(editor.getComponent());
            previewTabInfo.setText(title);
            previewTabInfo.setIcon(icon);

            if (actionGroup == null) {
                actionGroup = new DefaultActionGroup();
                actionGroup.add(new AutoSyncAction(this));
                actionGroup.add(new RefreshAction(this));
                actionGroup.add(new SelectOutputMimeTypeAction());
            }
            previewTabInfo.setActions(actionGroup, "DataSonnetPreview");
            outputTabs.getTabs().addTab(previewTabInfo);
            previewTabInfoMap.put(mimeType, previewTabInfo);
        }

        for (Map.Entry<String, TabInfo> entry : previewTabInfoMap.entrySet()) {
            entry.getValue().setHidden(!mimeType.equalsIgnoreCase(entry.getKey()));
        }

    }

    protected PsiFile getPsiFile() {
        return this.psiFile;
    }

    public void loadScenario(Scenario scenario) {
        //Create action group and toolbar
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new AddInputAction(this));
        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("Inputs", actionGroup, false);
        toolbar.setTargetComponent(this.getComponent());
        gui.getSourcePanel().add(toolbar.getComponent(), BorderLayout.WEST);

        closeAllInputs();
        final VirtualFile inputs = scenario.getInputs();
        if (inputs != null && inputs.isDirectory()) {
            loadInputFiles(inputs);
        }
    }

    public void loadInputFiles(VirtualFile inputsFolder) {
        closeAllInputs();
        List<VirtualFile> children = VfsUtil.collectChildrenRecursively(inputsFolder);
        for (VirtualFile input : children) {
            if (input.isDirectory()) {
                continue;
            }
            PsiFile file = PsiManager.getInstance(project).findFile(input);
            if (file == null) {
                continue;
            }

            Document document = file.getViewProvider().getDocument();
            if (document == null) {
                continue;
            }
            Editor editor = EditorFactory.getInstance().createEditor(document, project, input, false);
            editors.put(input.getCanonicalPath(), editor);
            TabInfo tabInfo = createTabInfo(inputsFolder, input, file, editor);
            inputTabs.getTabs().addTab(tabInfo);
            inputTabs.setSelectedIndex(0);

            try {
                addDocumentListener(editor.getDocument(), refreshPreview);
            } catch (Throwable e) {
            }
        }
    }

    @NotNull
    private TabInfo createTabInfo(VirtualFile inputs, VirtualFile input, PsiFile file, Editor editor) {
        TabInfo tabInfo = new TabInfo(inputTabs.getComponent());
        tabInfo.setPreferredFocusableComponent(null);
        ItemPresentation presentation = file.getPresentation();
        if (presentation != null) {
            final String relativeLocation = VfsUtil.getRelativeLocation(input, inputs);
            assert relativeLocation != null;
            String expression = relativeLocation.replace('/', '.');
            String extension = input.getExtension();
            if (extension != null) {
                //extension doesn't have the dot so we need to add a + 1
                expression = expression.substring(0, expression.length() - (extension.length() + 1)) + " (" + StringUtil.capitalize(extension) + ")";
            }
            tabInfo.setText(expression);
            tabInfo.setIcon(presentation.getIcon(false));
        }
        tabInfo.setComponent(editor.getComponent());
        return tabInfo;
    }

    public void closeAllInputs() {
        inputTabs.getTabs().removeAllTabs();
        for (Editor editor : editors.values()) {
            try {
                //Disposer.dispose(((EditorImpl) editor).getDisposable());
                EditorFactory.getInstance().releaseEditor(editor);
            } catch (Throwable e) {

            }
        }
    }

    private static void addDocumentListener(Document document, DocumentListener listener) throws Exception {
        Method method = document.getClass().getDeclaredMethod("getListeners");
        method.setAccessible(true);
        DocumentListener[] listeners = (DocumentListener[]) method.invoke(document);
        if (!Arrays.asList(listeners).contains(listener)) {
            document.addDocumentListener(listener);
        }
    }

    private class SelectScenarioAction extends AnAction {
        public SelectScenarioAction() {
            super(null, "Select scenario", IconLoader.findIcon("/icons/selectScenario.svg", SelectScenarioAction.class));
        }

        @Override
        public void actionPerformed(AnActionEvent e) {
            DefaultActionGroup group = new DefaultActionGroup();
            ScenarioManager manager = ScenarioManager.getInstance(project);

            java.util.List<Scenario> scenarios = manager.getScenariosFor(psiFile);

            addScenarioActions(group, scenarios);

            final InputEvent inputEvent = e.getInputEvent();
            final ActionPopupMenu popupMenu =
                    ((ActionManagerImpl) ActionManager.getInstance())
                            .createActionPopupMenu(TabbedPaneContentUI.POPUP_PLACE, group, new MenuItemPresentationFactory(false));

            int x = 0;
            int y = 0;
            if (inputEvent instanceof MouseEvent) {
                x = ((MouseEvent) inputEvent).getX();
                y = ((MouseEvent) inputEvent).getY();
            }
            popupMenu.getComponent().show(inputEvent.getComponent(), x, y);
        }

        private void addScenarioActions(DefaultActionGroup group, List<Scenario> scenarios) {
            ScenarioManager manager = ScenarioManager.getInstance(project);
            Scenario currentScenario = manager.getCurrentScenario(getPsiFile().getVirtualFile().getCanonicalPath());
            for (Scenario scenario : scenarios) {
                boolean isChecked = currentScenario != null && currentScenario.getName().equals(scenario.getName());
                group.add(new AnAction(scenario.getPresentableText(), scenario.getLocationString(), isChecked ? AllIcons.Actions.Checked : null) {
                    @Override
                    public void actionPerformed(AnActionEvent e) {
                        ScenarioManager.getInstance(project).setCurrentScenario(psiFile.getVirtualFile().getCanonicalPath(), scenario);
                        loadScenario(scenario);
                        gui.getInputsSplitter().setProportion(0.3f);
                        gui.getInputsSplitter().setEnabled(true);
                    }
                });
            }

        }
    }

    private class SelectOutputMimeTypeAction extends AnAction {
        public SelectOutputMimeTypeAction() {
            super(null, "Select Output Mime Type", IconLoader.findIcon("/icons/selectScenario.svg", SelectOutputMimeTypeAction.class));
        }

        @Override
        public void update(@NotNull final AnActionEvent e) {
            e.getPresentation().setEnabled(true);
        }

        @Override
        public void actionPerformed(AnActionEvent e) {
            DefaultActionGroup group = new DefaultActionGroup();

            group.add(new ToggleMimeType("JSON", "application/json"));
            group.add(new ToggleMimeType("YAML", "application/x-yaml"));
            group.add(new ToggleMimeType("XML", "application/xml"));
            group.add(new ToggleMimeType("CSV", "application/csv"));
            group.add(new ToggleMimeType("Plain Text", "text/plain"));


/*
            ServiceLoader<DataFormatPlugin> loader = ServiceLoader.load(DataFormatPlugin.class, Mapper.class.getClassLoader());
            Iterator<DataFormatPlugin> plugins = loader.iterator();

            while (plugins.hasNext()) {
                DataFormatPlugin plugin = plugins.next();

                final String dataFormatId = plugin.getPluginId();
                final String mimeType = plugin.getSupportedIdentifiers()[0];

                group.add(new ToggleMimeType(dataFormatId, mimeType));
            }
*/
            final InputEvent inputEvent = e.getInputEvent();
            final ActionPopupMenu popupMenu =
                    ((ActionManagerImpl) ActionManager.getInstance())
                            .createActionPopupMenu(TabbedPaneContentUI.POPUP_PLACE, group, new MenuItemPresentationFactory(false));

            int x = 0;
            int y = 0;
            if (inputEvent instanceof MouseEvent) {
                x = ((MouseEvent) inputEvent).getX();
                y = ((MouseEvent) inputEvent).getY();
            }
            popupMenu.getComponent().show(inputEvent.getComponent(), x, y);
        }

        @Override
        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.EDT;
        }

        class ToggleMimeType extends ToggleAction {

            ToggleMimeType(String format, String mimeType) {
                super(format, mimeType, null);
            }

            @Override
            public void update(@NotNull final AnActionEvent e) {
                super.update(e);
                e.getPresentation().setEnabled(true);
                e.getPresentation().setKeepPopupOnPerform(KeepPopupOnPerform.Never);
            }

            @Override
            public boolean isSelected(@NotNull AnActionEvent e) {
                String mimeType = e.getPresentation().getDescription();
                boolean isSelected = mimeType.equalsIgnoreCase(DataSonnetEditor.this.outputMimeType.toString());
                e.getPresentation().setIcon(isSelected ? AllIcons.Actions.Checked : null);
                return isSelected;
            }

            @Override
            public void setSelected(@NotNull AnActionEvent e, boolean state) {
                e.getPresentation().setIcon(state ? AllIcons.Actions.Checked : null);
                DataSonnetEditor.this.outputMimeType = MediaType.valueOf(e.getPresentation().getDescription());
                DataSonnetEditor.this.runPreview(true);
            }

            @Override
            public @NotNull ActionUpdateThread getActionUpdateThread() {
                return ActionUpdateThread.EDT;
            }
        }
    }
}