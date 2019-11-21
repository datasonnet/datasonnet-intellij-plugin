package com.modusbox.portx.datasonnet.editor;

import com.datasonnet.Mapper;
import com.datasonnet.StringDocument;
import com.datasonnet.portx.spi.DataFormatPlugin;
import com.datasonnet.portx.spi.DataFormatService;
import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.icons.AllIcons;
import com.intellij.json.JsonLanguage;
import com.intellij.lang.Language;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.navigation.ItemPresentation;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.impl.ActionManagerImpl;
import com.intellij.openapi.actionSystem.impl.MenuItemPresentationFactory;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.components.ServiceManager;
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
import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorImpl;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ui.configuration.ProjectSettingsService;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.impl.content.ToolWindowContentUi;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.ParameterizedCachedValue;
import com.intellij.ui.EditorNotificationPanel;
import com.intellij.ui.JBColor;
import com.intellij.ui.JBTabsPaneImpl;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.util.Alarm;
import com.modusbox.portx.datasonnet.config.DataSonnetProjectSettingsComponent;
import com.modusbox.portx.datasonnet.config.DataSonnetSettingsComponent;
import com.modusbox.portx.datasonnet.language.DataSonnetFileType;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.java.JavaResourceRootType;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

/**
 * Created by eberman on 11/3/16.
 */
public class DataSonnetEditor implements FileEditor {

    private Project project;
    private Module module;

    private PsiAwareTextEditorImpl textEditor;

    private PsiFile psiFile;

    private Map<String, Editor> editors = new HashMap<String, Editor>();
    private Map<String, String> contentTypes = new HashMap<String, String>();
    private Map<String, VirtualFile> inputOutputFiles = new HashMap<String, VirtualFile>();

    private JBTabsPaneImpl outputTabs;
    private JBTabsPaneImpl inputTabs;

    private TabInfo previewTabInfo;

    private DataSonnetEditorUI gui;

    final static Logger logger = Logger.getInstance(DataSonnetEditor.class);

    private final static long PREVIEW_DELAY = 500;

    Alarm myDocumentAlarm = new Alarm(Alarm.ThreadToUse.SWING_THREAD, this);
    DocumentListener refreshPreview;

    private boolean autoSync = false;

    private String outputMimeType = "application/json";

    private static final Key<ParameterizedCachedValue<Map<String, String>, VirtualFile>> DS_LIBRARIES_KEY = Key.create("DS_LIBRARIES");

    public DataSonnetEditor(@NotNull Project project, @NotNull VirtualFile virtualFile, final TextEditorProvider provider) {
        this.project = project;
        this.textEditor = new PsiAwareTextEditorImpl(project, virtualFile, provider);
        this.module = ModuleUtilCore.findModuleForFile(virtualFile, project);

        gui = new DataSonnetEditorUI(textEditor);

        inputTabs = new JBTabsPaneImpl(project, SwingConstants.TOP, this);
        inputTabs.getTabs().getPresentation().setSideComponentVertical(true);
        //inputTabs.getTabs().getPresentation().setEmptyText("No inputs available for the current scenario.\nAdd new input by clicking the + button.");

        gui.getSourcePanel().add(inputTabs.getComponent(), BorderLayout.CENTER);

        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new AnAction("Add new scenario", "Adds a new scenario for the current mapping", IconLoader.findIcon("/icons/addScenario.svg")) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                //Check if there is a test resources folder
                List<VirtualFile> testRoots = ModuleRootManager.getInstance(module).getSourceRoots(JavaResourceRootType.TEST_RESOURCE);
                if (testRoots == null || testRoots.isEmpty()) {
                    Notifications.Bus.notify(new Notification("DataSonnet",
                            "No Test Resources folder found!",
                            "Please mark at least one directory as <strong>Test Resources</strong> folder.\n <a href=\"projectSettings\">Click here</a> to open project settings.",
                            NotificationType.WARNING,
                            new NotificationListener() {
                                @Override
                                public void hyperlinkUpdate(@NotNull Notification notification, @NotNull HyperlinkEvent event) {
                                    if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                                        if ("projectSettings".equals(event.getDescription())) {
                                            ProjectSettingsService.getInstance(project).openModuleSettings(module);
                                        }
                                    }
                                }
                            }));

                    return;
                }

                final ScenarioManager manager = ScenarioManager.getInstance(project);
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
        });
        actionGroup.add(new SelectScenarioAction());

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("Scenarios", actionGroup, false);
        toolbar.getComponent().setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, JBColor.border()));

        gui.getRootPanel().add(toolbar.getComponent(), BorderLayout.WEST);

        outputTabs = new JBTabsPaneImpl(project, SwingConstants.TOP, this);
        outputTabs.getTabs().getPresentation().setSideComponentVertical(true);
        //((JBTabsImpl) outputTabs.getTabs()).setSideComponentVertical(true);
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
                            new WriteCommandAction.Simple(project, psiFile) {
                                @Override
                                protected void run() throws Throwable {
                                    runPreview(false);
                                }
                            }.execute();
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
        /*
            final DataSonnetFile dataSonnetFile = (DataSonnetFile) psiFile;

            PsiManager.getInstance(project).addPsiTreeChangeListener(new PsiTreeChangeAdapter() {
                @Override
                public void childReplaced(@NotNull PsiTreeChangeEvent event) {
                    super.childReplaced(event);

                    if (event.getFile() != psiFile && !(event.getFile() instanceof DataSonnetFile))
                        return;

                    textEditor.getPreferredFocusedComponent().grabFocus();

                    if (myDocumentAlarm.isDisposed())
                        return;

                    myDocumentAlarm.cancelAllRequests();
                    myDocumentAlarm.addRequest(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                new WriteCommandAction.Simple(project, psiFile) {
                                    @Override
                                    protected void run() throws Throwable {
                                        runPreview(false);
                                    }
                                }.execute();
                            } catch (Exception e) {
                                logger.error(e);
                            }

                        }
                    }, PREVIEW_DELAY);
                }
            });
            */

            //TODO - list scenarios for file, if there are any, load first one and display
            ScenarioManager manager = ScenarioManager.getInstance(project);

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
            }
            else {
                app.invokeAndWait(action, ModalityState.current());
            }

            createOutputTab();
            this.runPreview(true);
        }

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
            EditorFactory.getInstance().releaseEditor(editor);
        }
        Disposer.dispose(textEditor);
        Disposer.dispose(this);
    }

    @Nullable
    @Override
    public <T> T getUserData(@NotNull Key<T> key) {
        return null;
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T t) {

    }

    private com.datasonnet.Document runPreviewBuiltIn() {
        ScenarioManager manager = ScenarioManager.getInstance(project);
        Scenario currentScenario = manager.getCurrentScenario(getPsiFile().getVirtualFile().getCanonicalPath());

        if (currentScenario == null)
            return new StringDocument("ERROR: No mapping scenarios available!", "text/plain");

        String dataSonnetScript = this.textEditor.getEditor().getDocument().getText();

        String payload = "{}";

        Map<String, VirtualFile> inputFiles = currentScenario.getInputFiles();
        HashMap<String, com.datasonnet.Document> variables = new HashMap<>();

        String payloadMimeType = "application/json";

        for (Map.Entry<String, VirtualFile> f : inputFiles.entrySet()) {

            String contents = null;
            try {
                contents = new String(f.getValue().contentsToByteArray());
                if (f.getKey().equals("payload")) {
                    payload = contents;
                    payloadMimeType = getMimeTypeByExtension(f.getValue().getExtension());
                }
                else {
                    variables.put(f.getKey(), new StringDocument(contents, getMimeTypeByExtension(f.getValue().getExtension())));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Map<String, String> libraries = getDSLibraries(this.textEditor.getFile());

        try {
            ClassLoader currentCL = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(Mapper.class.getClassLoader());

            Mapper mapper = new Mapper(dataSonnetScript, variables.keySet(), libraries,true);
            com.datasonnet.Document transformDoc = mapper.transform(new StringDocument(payload, payloadMimeType), variables, outputMimeType);

            Thread.currentThread().setContextClassLoader(currentCL);

            return transformDoc;
        } catch (Exception e) {
            return new StringDocument(e.getMessage(), "text/plain");
        }
    }

    private String runPreviewExt() {
        ScenarioManager manager = ScenarioManager.getInstance(project);
        Scenario currentScenario = manager.getCurrentScenario(getPsiFile().getVirtualFile().getCanonicalPath());

        if (currentScenario == null)
            return "ERROR: No mapping scenarios available!";

        String output = null;

        DataSonnetSettingsComponent mySettingsComponent = ServiceManager.getService(DataSonnetSettingsComponent.class);
        String pathToDataSonnet = mySettingsComponent.getState().getDataSonnetExecPath();
        String pathToMappingFile = psiFile.getVirtualFile().getCanonicalPath();

        //TODO Assert not null, display message - com.intellij.openapi.options.ShowSettingsUtil
        if (pathToDataSonnet == null ||
                StringUtils.isEmpty(pathToDataSonnet) ||
                !(new File(pathToDataSonnet).exists() && !new File(pathToDataSonnet).isDirectory())) {
            EditorNotificationPanel panel = new EditorNotificationPanel();
            panel.setText("Unable to find Datasonnt/DataSonnet executable in path.");
            panel.createActionLabel("Configure DataSonnet path", new Runnable() {
                @Override
                public void run() {
                    editors.get("Preview").setHeaderComponent(null);
                    ShowSettingsUtil.getInstance().showSettingsDialog(project, "datasonnet");
                }
            });

            editors.get("Preview").setHeaderComponent(panel);

            //return "Unable to find DataSonnet executable in path. Please configure the DataSonnet path at Tools -> DataSonnet";
            return "";
        }

        editors.get("Preview").setHeaderComponent(null);

        java.util.List<String> args = new ArrayList<String>();
        args.add(pathToDataSonnet);

        DataSonnetProjectSettingsComponent myProjectSettingsComponent = ServiceManager.getService(project, DataSonnetProjectSettingsComponent.class);
        for (String path : myProjectSettingsComponent.getState().getDataSonnetLibraryPaths()) {
            args.add("-J");
            args.add(path);
        }

        Map<String, VirtualFile> inputFiles = currentScenario.getInputFiles();

        String dataSonnetScriptHeader = "";

        boolean isExtVars = mySettingsComponent.getState().isExtVars();
        if (isExtVars) {
            for (Map.Entry<String, VirtualFile> f : inputFiles.entrySet()) {
                args.add("--ext-code-file");
                args.add(f.getKey() + "=" + f.getValue().getCanonicalPath());
                dataSonnetScriptHeader = dataSonnetScriptHeader + "local " + f.getKey() + " = std.extVar(\"" + f.getKey() + "\");\n";
            }
        } else {
            for (Map.Entry<String, VirtualFile> f : inputFiles.entrySet()) {
                args.add("--tla-code-file");
                args.add(f.getKey() + "=" + f.getValue().getCanonicalPath());
                dataSonnetScriptHeader = dataSonnetScriptHeader + (StringUtils.isEmpty(dataSonnetScriptHeader) ? "function(" : ", ") + f.getKey();
            }
            dataSonnetScriptHeader = dataSonnetScriptHeader + ") \n";
        }

        String dataSonnetScript = dataSonnetScriptHeader + this.textEditor.getEditor().getDocument().getText();
        args.add("-e");
        args.add(dataSonnetScript);

        try {
            Process proc = Runtime.getRuntime().exec(args.toArray(new String[]{}));
            InputStream is = proc.getInputStream();
            Scanner s = new Scanner(is).useDelimiter("\\A");

            if (s.hasNext()) {
                output = s.next();
            } else {
                InputStream es = proc.getErrorStream();
                s = new Scanner(es).useDelimiter("\\A");
                if (s.hasNext()) {
                    output = s.next();
                } else {
                    output = "";
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        final String preview = output;

        return preview;
    }

    protected void runPreview(boolean forceRefresh) {
        if (!isAutoSync() && !forceRefresh)
            return;

        //Commit all data
        new WriteCommandAction.Simple(project, psiFile) {
            @Override
            protected void run() throws Throwable {
                for (Editor nextEditor : editors.values()) {
                    PsiDocumentManager.getInstance(project).commitDocument(nextEditor.getDocument());
                    FileDocumentManager.getInstance().saveDocument(nextEditor.getDocument());
                }

            }
        }.execute();

        this.textEditor.getEditor().getMarkupModel().removeAllHighlighters();

        DataSonnetSettingsComponent mySettingsComponent = ServiceManager.getService(DataSonnetSettingsComponent.class);
        boolean useBuiltIn = mySettingsComponent.getState().isBuiltInParser();

        final String contentType;
        final String preview;

        if (useBuiltIn) {
            com.datasonnet.Document doc = runPreviewBuiltIn();
            preview = doc.contents();
            contentType = doc.mimeType();
        } else {
            preview = runPreviewExt();
            contentType = "application/json";
        }

        if (preview != null) {
            if (preview.startsWith("Problem")) {
                Document document = this.textEditor.getEditor().getDocument();

                try {
                    String errLine = preview.split("\n")[1];
                    String line = errLine.substring(errLine.indexOf("line"), errLine.indexOf("column"));
                    String column = errLine.substring(errLine.indexOf("column"));

                    line = line.replaceAll("[^\\d.]", "");
                    column = column.replaceAll("[^\\d.]", "");

                    int lineNumber = new Integer(line).intValue() - 1;
                    int columnNumber = new Integer(column).intValue();

                    int startOffset = document.getLineStartOffset(lineNumber) + columnNumber;
                    int endOffset = document.getLineEndOffset(lineNumber);

                    TextAttributes err = new TextAttributes(null, null, Color.RED, EffectType.WAVE_UNDERSCORE, Font.PLAIN);

                    if (startOffset < endOffset) {
                        RangeHighlighter rangeHighlighter = this.textEditor.getEditor().getMarkupModel().addRangeHighlighter(startOffset, endOffset, 0, err, HighlighterTargetArea.EXACT_RANGE);
                        rangeHighlighter.setErrorStripeMarkColor(Color.RED);
                    }
                } catch (Exception e) {

                }
            }

            try {
                new WriteCommandAction.Simple(project, psiFile) {
                    @Override
                    protected void run() throws Throwable {
                        updateOutputTab(preview, contentType);
                    }
                }.execute();
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
        DataFormatService.getInstance().findAndRegisterPlugins();

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
        } else {
            icon = AllIcons.FileTypes.Text;
            language = PlainTextLanguage.INSTANCE;
        }

        String title = "Preview";
        PsiFile f = PsiFileFactory.getInstance(getProject()).createFileFromText(language, contents);
        inputOutputFiles.put(title, f.getVirtualFile());

        Editor oldEditor = editors.get(title);
        if (oldEditor != null) {
            EditorFactory.getInstance().releaseEditor(oldEditor);
        }
        final Editor editor = EditorFactory.getInstance().createEditor(f.getViewProvider().getDocument(), getProject(), language.getAssociatedFileType(), true);
        editors.put(title, editor);

        final Application app = ApplicationManager.getApplication();
        Runnable action = new Runnable() {
            @Override
            public void run() {
                app.runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                        editor.getDocument().setText(contents);
                    }
                });
            }
        };
        if (app.isDispatchThread()) {
            action.run();
        }
        else {
            app.invokeAndWait(action, ModalityState.current());
        }

        FileType newType = language.getAssociatedFileType();
        ((EditorEx) editor).setHighlighter(EditorHighlighterFactory.getInstance().createEditorHighlighter(project, newType));
        ((EditorEx) editor).setFile(f.getVirtualFile());

        PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());
        PsiFile previewPsiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        CodeStyleManager.getInstance(project).reformat(previewPsiFile);

        outputTabs.getTabs().getPresentation().setSideComponentVertical(true);

        try {
            outputTabs.removeTabAt(outputTabs.getTabs().getIndexOf(previewTabInfo));
        } catch (Exception e) {

        }
        previewTabInfo = new TabInfo(editor.getComponent());
        previewTabInfo.setText(title);
        previewTabInfo.setIcon(icon);

        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new AutoSyncAction(this));
        actionGroup.add(new RefreshAction(this));
        actionGroup.add(new SelectOutputMimeTypeAction());
        previewTabInfo.setActions(actionGroup, "DataSonnetPreview");
        outputTabs.getTabs().addTab(previewTabInfo);
    }

    protected PsiFile getPsiFile() {
        return this.psiFile;
    }

    public void loadScenario(Scenario scenario) {
        //Create action group and toolbar
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new AddInputAction(this));
        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("Inputs", actionGroup, false);
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
                editor.getDocument().addDocumentListener(refreshPreview);
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
    }

    private String getMimeTypeByExtension(String extension) {
        String inputMimeType = "application/json";

        if ("csv".equalsIgnoreCase(extension)) {
            inputMimeType = "application/csv";
        } else if ("xml".equalsIgnoreCase(extension)) {
            inputMimeType = "application/xml";
        }

        return inputMimeType;
    }

    @NotNull
    private Map<String, String> getDSLibraries(@NotNull final VirtualFile ds) {
        Map<String, String> libraries = new HashMap();

        Collection<VirtualFile> libs = FilenameIndex.getAllFilesByExt(project, "libsonnet", GlobalSearchScope.allScope(project));
        for (VirtualFile nextLib : libs) {
            try {
                String content = VfsUtil.loadText(nextLib);
                String path = nextLib.getPath();
                if (path.toLowerCase().contains(".jar!")) {
                    path = path.substring(path.lastIndexOf("!") + 1);
                } else {
                    List<VirtualFile> roots = new ArrayList(Arrays.asList(ModuleRootManager.getInstance(module).getSourceRoots()));
                    roots.addAll(Arrays.asList(ModuleRootManager.getInstance(module).getContentRoots()));

                    for (VirtualFile root : roots) {
                        if (path.startsWith(root.getPath())) {
                            path = path.replace(root.getPath(), "");
                            break;
                        }
                    }
                }
                if (path.startsWith("/")) {
                    path = path.substring(1);
                }
                libraries.put(path, content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return libraries;
    }

    private class SelectScenarioAction extends AnAction {
        public SelectScenarioAction() {
            super(null, "Select scenario", IconLoader.findIcon("/icons/selectScenario.svg"));
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
                            .createActionPopupMenu(ToolWindowContentUi.POPUP_PLACE, group, new MenuItemPresentationFactory(false));

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
                    }
                });
            }

        }
    }

    private class SelectOutputMimeTypeAction extends AnAction {
        public SelectOutputMimeTypeAction() {
            super(null, "Select Output Mime Type", IconLoader.findIcon("/icons/selectScenario.svg"));
        }

        @Override
        public void update(@NotNull final AnActionEvent e) {
            DataSonnetSettingsComponent mySettingsComponent = ServiceManager.getService(DataSonnetSettingsComponent.class);
            boolean useBuiltIn = mySettingsComponent.getState().isBuiltInParser();
            e.getPresentation().setEnabled(useBuiltIn);
        }

        @Override
        public void actionPerformed(AnActionEvent e) {
            DefaultActionGroup group = new DefaultActionGroup();

            group.add(new ToggleMimeType("JSON", "application/json"));

            ServiceLoader<DataFormatPlugin> loader = ServiceLoader.load(DataFormatPlugin.class, Mapper.class.getClassLoader());
            Iterator<DataFormatPlugin> plugins = loader.iterator();

            while (plugins.hasNext()) {
                DataFormatPlugin plugin = plugins.next();

                final String dataFormatId = plugin.getPluginId();
                final String mimeType = plugin.getSupportedMimeTypes()[0];

                group.add(new ToggleMimeType(dataFormatId, mimeType));
            }

            final InputEvent inputEvent = e.getInputEvent();
            final ActionPopupMenu popupMenu =
                    ((ActionManagerImpl) ActionManager.getInstance())
                            .createActionPopupMenu(ToolWindowContentUi.POPUP_PLACE, group, new MenuItemPresentationFactory(false));

            int x = 0;
            int y = 0;
            if (inputEvent instanceof MouseEvent) {
                x = ((MouseEvent) inputEvent).getX();
                y = ((MouseEvent) inputEvent).getY();
            }
            popupMenu.getComponent().show(inputEvent.getComponent(), x, y);
        }

        class ToggleMimeType extends ToggleAction {
            ToggleMimeType(String format, String mimeType) {
                super(format, mimeType, null);
            }
            @Override
            public void update(@NotNull final AnActionEvent e) {
                DataSonnetSettingsComponent mySettingsComponent = ServiceManager.getService(DataSonnetSettingsComponent.class);
                boolean useBuiltIn = mySettingsComponent.getState().isBuiltInParser();
                e.getPresentation().setEnabled(useBuiltIn);
            }
            @Override
            public boolean isSelected(@NotNull AnActionEvent e) {
                String mimeType = e.getPresentation().getDescription();
                return mimeType.equalsIgnoreCase(DataSonnetEditor.this.outputMimeType);
            }

            @Override
            public void setSelected(@NotNull AnActionEvent e, boolean state) {
                DataSonnetEditor.this.outputMimeType = e.getPresentation().getDescription();
                DataSonnetEditor.this.runPreview(true);
            }
        }
    }
}