package io.portx.datasonnet.actions;

import com.intellij.ide.actions.CreateFileAction;
import com.intellij.openapi.project.DumbAware;
import io.portx.datasonnet.language.DataSonnetIcon;

public class CreateNewFileAction extends CreateFileAction implements DumbAware {

    public CreateNewFileAction() {
        super("DataSonnet File", "Create New DataSonnet File.", DataSonnetIcon.FILE);
    }

    @Override
    protected String getDefaultExtension() {
        return "ds";
    }

//    @Override
//    protected void buildDialog(Project project, PsiDirectory psiDirectory, CreateFileFromTemplateDialog.Builder builder) {
//        builder.setTitle("RAML File")
//                .addKind("API Specification", RamlIcons.RamlFileType, RamlFilesTemplateManager.RAML_FILE)
//                .addKind("Annotation Type Declaration", RamlIcons.RamlFileType, RamlFilesTemplateManager.ANNOTATION_TYPE_DECLARATION)
//                .addKind("Data Type", RamlIcons.RamlFileType, RamlFilesTemplateManager.DATA_TYPE)
//                .addKind("Documentation Item", RamlIcons.RamlFileType, RamlFilesTemplateManager.DOCUMENTATION_ITEM)
//                .addKind("Extension", RamlIcons.RamlFileType, RamlFilesTemplateManager.EXTENSION)
//                .addKind("Library", RamlIcons.RamlFileType, RamlFilesTemplateManager.LIBRARY)
//                .addKind("Named Example", RamlIcons.RamlFileType, RamlFilesTemplateManager.NAMED_EXAMPLE)
//                .addKind("Overlay", RamlIcons.RamlFileType, RamlFilesTemplateManager.OVERLAY)
//                .addKind("Resource Type", RamlIcons.RamlFileType, RamlFilesTemplateManager.RESOURCE_TYPE)
//                .addKind("Security Scheme", RamlIcons.RamlFileType, RamlFilesTemplateManager.SECURITY_SCHEME)
//                .addKind("Trait", RamlIcons.RamlFileType, RamlFilesTemplateManager.TRAIT);
//    }
//
//    @Override
//    protected String getActionName(PsiDirectory directory, String newName, String templateName) {
//        return "Create " + newName;
//    }

//    @Override
//    public int hashCode() {
//        return 0;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        return obj instanceof CreateNewFileAction;
//    }
}

