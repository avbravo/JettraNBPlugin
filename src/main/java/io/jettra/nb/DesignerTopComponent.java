package io.jettra.nb;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

@ConvertAsProperties(
        dtd = "-//io.jettra.nb//Designer//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "DesignerTopComponent",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "io.jettra.nb.DesignerTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_DesignerAction",
        preferredID = "DesignerTopComponent"
)
@Messages({
    "CTL_DesignerAction=Jettra Designer",
    "CTL_DesignerTopComponent=Jettra Designer Window",
    "HINT_DesignerTopComponent=This is a Jettra Designer window"
})
public final class DesignerTopComponent extends TopComponent {

    private JFXPanel fxPanel;
    private WebEngine webEngine;
    private String currentProjectPath;

    public DesignerTopComponent() {
        initComponents();
        setName(Bundle.CTL_DesignerTopComponent());
        setToolTipText(Bundle.HINT_DesignerTopComponent());
        
        setLayout(new BorderLayout());
        
        fxPanel = new JFXPanel();
        add(fxPanel, BorderLayout.CENTER);
        
        Platform.setImplicitExit(false);
        Platform.runLater(this::initFX);
    }

    public void setWorkingDirectory(String name, String path) {
        this.currentProjectPath = path;
        Platform.runLater(() -> {
            webEngine.executeScript("setProjectInfo('" + name + "', '" + path.replace("\\", "/") + "')");
        });
    }

    public void loadModelsToDesigner(String jsonModels) {
        Platform.runLater(() -> {
            webEngine.executeScript("loadWorkspaceFiles('" + jsonModels.replace("\\", "\\\\").replace("'", "\\'") + "')");
        });
    }

    private void initFX() {
        WebView webView = new WebView();
        webEngine = webView.getEngine();
        
        webEngine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaConnector", new JavaConnector());
            }
        });
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("index.html"), StandardCharsets.UTF_8))) {
            String html = reader.lines().collect(Collectors.joining("\n"));
            webEngine.loadContent(html);
        } catch (Exception e) {
            e.printStackTrace();
            webEngine.loadContent("<html><body>Error loading UI</body></html>");
        }
        
        Scene scene = new Scene(webView);
        fxPanel.setScene(scene);
    }

    public class JavaConnector {
        public void showInfo(String text) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(text, NotifyDescriptor.INFORMATION_MESSAGE));
        }

        public void showError(String text) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(text, NotifyDescriptor.ERROR_MESSAGE));
        }

        public void saveCode(String className, String code) {
            if (currentProjectPath == null) {
                showError("No project selected. Please right-click a project and select 'Jettra Designer' first.");
                return;
            }
            
            try {
                // Determine target directory based on class name or type
                String subPath = "src/main/java/com/jettra/example/pages";
                if (className.endsWith("Model")) {
                    subPath = "src/main/java/com/jettra/example/model";
                }
                
                java.io.File targetDir = new java.io.File(currentProjectPath, subPath);
                if (!targetDir.exists()) {
                    targetDir.mkdirs();
                }
                
                java.io.File targetFile = new java.io.File(targetDir, className + ".java");
                
                if (targetFile.exists()) {
                    NotifyDescriptor d = new NotifyDescriptor.Confirmation(
                        "El archivo " + className + ".java ya existe. ¿Desea reemplazarlo con los nuevos cambios?",
                        "Confirmar Reemplazo",
                        NotifyDescriptor.YES_NO_OPTION
                    );
                    if (DialogDisplayer.getDefault().notify(d) != NotifyDescriptor.YES_OPTION) {
                        return;
                    }
                }
                
                java.nio.file.Files.writeString(targetFile.toPath(), code);
                showInfo("Archivo guardado exitosamente en: " + targetFile.getAbsolutePath());
            } catch (Exception e) {
                showError("Error al guardar el archivo: " + e.getMessage());
                e.printStackTrace();
            }
        }

        public void generateCode(String code) {
            showInfo("Generated Code (Preview):\n" + (code.length() > 500 ? code.substring(0, 500) + "..." : code));
        }

        public void requestWorkspaceFiles() {
            if (currentProjectPath == null) {
                showInfo("No project selected. Right-click a project in the Project explorer and select 'Jettra Designer'.");
                return;
            }
            // Logic handled by JettraDesignerAction but kept for compatibility
        }
    }

    private void initComponents() {
    }

    @Override
    public void componentOpened() {
    }

    @Override
    public void componentClosed() {
    }

    void writeProperties(java.util.Properties p) {
        p.setProperty("version", "1.0");
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
    }
}
