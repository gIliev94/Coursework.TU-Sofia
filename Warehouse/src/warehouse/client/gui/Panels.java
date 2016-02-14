package warehouse.client.gui;

import java.awt.Color;
import java.awt.Cursor;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Configuration class - sets up properties of PANELS.
 * 
 * @author Georgi Iliev
 *
 */
public class Panels {

    private Panels() {
    }

    /**
     * Setup the common properties for unlimited number of panels and add them
     * to the specified form.
     * 
     * @param clientForm
     * @param panels
     */
    private static void configure(JFrame clientForm, JPanel... panels) {
	for (JPanel panel : panels) {
	    panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    panel.setLayout(null);
	    panel.setBackground(Color.BLACK);
	    clientForm.getContentPane().add(panel);
	}
    }

    /**
     * Configures the panels for the application.
     * 
     * @param clientForm
     * @param panelMain
     * @param panelAuthentication
     * @param panelProfiles
     * @param panelHistories
     */
    static void configurePanels(JFrame clientForm, JPanel panelMain, JPanel panelAuthentication, JPanel panelProfiles,
	    JPanel panelHistories) {
	configure(clientForm, panelMain, panelAuthentication, panelProfiles, panelHistories);

	panelMain.setName("MAIN");
	panelMain.setToolTipText("Main UI view - handles ordering and dispatchment to other panels");

	panelAuthentication.setName("AUTHENTICATION");
	panelAuthentication.setToolTipText("Authentication- middle step in dispcathment to restricted access views");

	panelProfiles.setName("PROFILES");
	panelProfiles.setToolTipText("Profiles info - generic and per client views.");

	panelHistories.setName("HISTORIES");
	panelHistories.setToolTipText("Historic view of discounts - generic and per client.");
    }

}
