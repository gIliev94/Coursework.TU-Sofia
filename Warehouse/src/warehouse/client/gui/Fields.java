package warehouse.client.gui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Configuration class - sets up properties of FIELDS.
 * 
 * @author Georgi Iliev
 *
 */
public class Fields {

    private Fields() {
    }

    /**
     * Setup the common properties for unlimited number of fields add add them
     * to the specified panel.
     * 
     * @param fields
     */
    private static void configure(JPanel panel, JTextField... fields) {

	for (JTextField field : fields) {
	    field.setSelectionColor(Color.BLUE);
	    field.setSelectedTextColor(Color.WHITE);
	    field.setFont(new Font("Rockwell", Font.BOLD, 12));

	    panel.add(field);
	}
    }

    /**
     * Configures the fields for panel: MAIN.
     * 
     * @param panelMain
     * @param clientIdField
     * @param quantityField
     */
    static void configureMainFields(JPanel panelMain, JTextField clientIdField, JTextField quantityField) {
	configure(panelMain, clientIdField, quantityField);

	clientIdField.setBounds(61, 100, 86, 27);
	clientIdField.setToolTipText("Enter your ID here");

	quantityField.setBounds(61, 154, 86, 27);
	quantityField.setToolTipText("Enter desired order quantity here");
    }

    /**
     * Configures the fields for panel: PROFILES.
     * 
     * @param panelProfiles
     * @param clientFieldProfiles
     */
    static void configureProfilesFields(JPanel panelProfiles, JTextField clientFieldProfiles) {
	configure(panelProfiles, clientFieldProfiles);

	clientFieldProfiles.setBounds(33, 151, 86, 20);
	clientFieldProfiles.setToolTipText("Enter client`s ID here");
    }

    /**
     * Configures the fields for panel: HISTORIES.
     * 
     * @param panelHistories
     * @param clientNameField
     */
    static void configureHistoriesFields(JPanel panelHistories, JTextField clientNameField) {
	configure(panelHistories, clientNameField);

	clientNameField.setBounds(319, 143, 141, 30);
	clientNameField.setToolTipText("Enter client`s ID here");
    }

    /**
     * Configures the fields for panel: AUTHENTICATION.
     * 
     * @param panelAuthentication
     * @param clientNameField
     */
    static void configureAuthenticationFields(JPanel panelAuthentication, JTextField usernameField,
	    JTextField passwordField) {
	configure(panelAuthentication, usernameField, passwordField);

	usernameField.setBounds(122, 102, 220, 29);
	usernameField.setToolTipText("Enter your username here");

	passwordField.setBounds(122, 167, 220, 41);
	passwordField.setToolTipText("Enter your password here");
    }

}
