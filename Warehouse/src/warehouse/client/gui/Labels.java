package warehouse.client.gui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Configuration class - sets up properties of LABELS.
 * 
 * @author Georgi Iliev
 *
 */
public class Labels {

    private Labels() {
    }

    /**
     * Setup the common properties for unlimited number of labels and add them
     * to the specified panel.
     * 
     * @param panel
     * @param labels
     */
    private static void configure(JPanel panel, JLabel... labels) {
	for (JLabel label : labels) {
	    label.setForeground(Color.YELLOW);
	    panel.add(label);
	}
    }

    /**
     * Configures the labels for panel: MAIN.
     * 
     * @param panelMain
     * @param clientLabel
     * @param productLabel
     * @param sectionLabel
     * @param quantityLabel
     * @param outputLabel
     */
    static void configureMainLabels(JPanel panelMain, JLabel clientLabel, JLabel productLabel, JLabel sectionLabel,
	    JLabel quantityLabel, JLabel outputLabel) {
	configure(panelMain, clientLabel, productLabel, sectionLabel, quantityLabel, outputLabel);

	clientLabel.setBounds(71, 75, 59, 27);
	clientLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));

	productLabel.setBounds(69, 8, 108, 27);
	productLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));

	sectionLabel.setBounds(0, 273, 487, 14);
	sectionLabel.setFont(new Font("Tahoma", Font.BOLD, 14));

	outputLabel.setBounds(284, 21, 100, 14);
	outputLabel.setFont(new Font("Tahoma", Font.BOLD, 14));

	quantityLabel.setBounds(71, 130, 59, 27);
	quantityLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));
    }

    /**
     * Configures the labels for panel: PROFILES.
     * 
     * @param clientIdTextFieldOrders
     * @param panelProfiles
     * @param outputProfilesLabel
     * @param personalProfileLabel
     * @param profileOfAllLabel
     * @param clientIdLabelOrders
     */
    static void configureProfilesLabels(JPanel panelProfiles, JLabel outputProfilesLabel, JLabel personalProfileLabel,
	    JLabel profileOfAllLabel, JLabel genericProfileLabel) {
	configure(panelProfiles, outputProfilesLabel, personalProfileLabel, profileOfAllLabel, genericProfileLabel);

	outputProfilesLabel.setBounds(144, 11, 208, 14);
	outputProfilesLabel.setFont(new Font("Tahoma", Font.BOLD, 14));

	personalProfileLabel.setBounds(208, 165, 166, 14);
	personalProfileLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));

	profileOfAllLabel.setBounds(205, 52, 147, 14);
	profileOfAllLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));

	genericProfileLabel.setBounds(32, 133, 73, 14);
	genericProfileLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));

    }

    /**
     * Configures the labels for panel: HISTORIES.
     * 
     * @param panelHistories
     * @param clientIdLabelDiscounts
     * @param dicountsLabel
     */
    static void configureHistoriesLabels(JPanel panelHistories, JLabel clientIdLabelDiscounts, JLabel dicountsLabel) {
	configure(panelHistories, clientIdLabelDiscounts, dicountsLabel);

	clientIdLabelDiscounts.setBounds(319, 121, 84, 14);
	clientIdLabelDiscounts.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));

	dicountsLabel.setBounds(82, 14, 162, 14);
	dicountsLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
    }

    /**
     * Configures the labels for panel: AUTHENTICATION.
     * 
     * @param panelAuthentication
     * @param passwordLabel
     * @param usernameLabel
     * @param loginLabel
     */
    static void configureAuthenticationLabels(JPanel panelAuthentication, JLabel passwordLabel, JLabel usernameLabel,
	    JLabel loginLabel) {
	configure(panelAuthentication, passwordLabel, usernameLabel, loginLabel);

	passwordLabel.setBounds(122, 142, 96, 14);
	passwordLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));

	usernameLabel.setBounds(122, 77, 113, 14);
	usernameLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));

	loginLabel.setBounds(147, 23, 199, 14);
	loginLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
    }

}
