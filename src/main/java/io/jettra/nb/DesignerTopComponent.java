package io.jettra.nb;

import java.awt.BorderLayout;
import org.netbeans.api.settings.ConvertAsProperties;
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

    public DesignerTopComponent() {
        initComponents();
        setName(Bundle.CTL_DesignerTopComponent());
        setToolTipText(Bundle.HINT_DesignerTopComponent());
        
        setLayout(new BorderLayout());
        // In a real implementation, we would add a JFXPanel with a WebView here
        // to host the same designer HTML/JS from WebDesignerPage.java
    }

    private void initComponents() {
        // ... NetBeans auto-generated code usually goes here
    }

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        p.setProperty("version", "1.0");
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
    }
}
