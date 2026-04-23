package io.jettra.nb;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.netbeans.api.project.Project;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

@ActionID(
        category = "Project",
        id = "io.jettra.nb.JettraDesignerAction"
)
@ActionRegistration(
        displayName = "#CTL_JettraDesignerAction"
)
@ActionReference(path = "Projects/Actions", position = 0)
@Messages("CTL_JettraDesignerAction=Jettra Designer")
public final class JettraDesignerAction implements ActionListener {

    private final Project context;

    public JettraDesignerAction(Project context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        FileObject projectDirectory = context.getProjectDirectory();
        String projectPath = projectDirectory.getPath();
        String projectName = projectDirectory.getName();

        DesignerTopComponent tc = (DesignerTopComponent) WindowManager.getDefault().findTopComponent("DesignerTopComponent");
        if (tc != null) {
            tc.open();
            tc.requestActive();
            tc.setWorkingDirectory(projectName, projectPath);
            loadProjectModels(tc, projectPath);
        }
    }

    private void loadProjectModels(DesignerTopComponent tc, String projectPath) {
        List<String> modelFiles = new ArrayList<>();
        File root = new File(projectPath);
        
        try (Stream<Path> walk = Files.walk(root.toPath())) {
            List<Path> result = walk.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith("Model.java"))
                    .collect(Collectors.toList());

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < result.size(); i++) {
                Path p = result.get(i);
                String name = p.getFileName().toString();
                String content = Files.readString(p);
                
                // Manual escaping for a simple JSON array
                String escapedContent = content
                        .replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\b", "\\b")
                        .replace("\f", "\\f")
                        .replace("\n", "\\n")
                        .replace("\r", "\\r")
                        .replace("\t", "\\t");
                
                json.append("{")
                    .append("\"path\": \"").append(p.toString().replace("\\", "/")).append("\", ")
                    .append("\"content\": \"").append(escapedContent).append("\"")
                    .append("}");
                
                if (i < result.size() - 1) json.append(",");
            }
            json.append("]");
            
            tc.loadModelsToDesigner(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
