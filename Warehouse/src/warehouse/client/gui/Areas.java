package warehouse.client.gui;

import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Configuration class - sets up properties of AREAS.
 * 
 * @author Georgi Iliev
 *
 */
public class Areas {

    private Areas() {
    }

    /**
     * Setup the common properties for unlimited number of areas and add them to
     * the specified panel.
     * 
     * @param panel
     * @param areas
     */
    private static void configure(JPanel panel, JTextArea... areas) {
	for (JTextArea area : areas) {
	    area.setFont(new Font("Tahoma", Font.PLAIN, 12));
	    area.setEditable(false);
	    area.setFocusable(false);
	    panel.add(area);
	}
    }

    /**
     * Configures the areas for panel: MAIN.
     * 
     * @param panelMain
     * @param outputArea
     */
    static void configureMainAreas(JPanel panelMain, JTextArea outputArea) {
	configure(panelMain, outputArea);

	outputArea.setBounds(231, 43, 233, 147);
	outputArea.setToolTipText("Shows response on order completion");

	JScrollPane scrollPane = new JScrollPane();
	scrollPane.setBounds(231, 43, 233, 147);
	panelMain.add(scrollPane);
	scrollPane.setViewportView(outputArea);
    }

    /**
     * Configures the areas for panel: PROFILES.
     * 
     * @param panelProfiles
     * @param personalProfileArea
     * @param genericProfileArea
     */
    static void configureProfilesAreas(JPanel panelProfiles, JTextArea personalProfileArea, JTextArea genericProfileArea) {
	configure(panelProfiles, personalProfileArea, genericProfileArea);

	genericProfileArea.setBounds(205, 70, 252, 39);
	genericProfileArea.setToolTipText("Shows statistics for ALL orders and profits");

	personalProfileArea.setBounds(205, 182, 252, 39);
	personalProfileArea.setToolTipText("Shows statistics for orders and profits from SPECIFIED(by name client");
    }

    /**
     * Configures the areas for panel: HISTORIES.
     * 
     * @param panelHistories
     * @param discountsHistoryArea
     */
    static void configureHistoriesAreas(JPanel panelHistories, JTextArea discountsHistoryArea) {
	configure(panelHistories, discountsHistoryArea);

	discountsHistoryArea.setBounds(33, 39, 266, 200);
	discountsHistoryArea.setToolTipText("Shows statistics for discounts");
    }

}
