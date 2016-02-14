package warehouse.client.gui;

import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Configuration class - sets up properties of BUTTONS.
 * 
 * @author Georgi Iliev
 *
 */
public class Buttons {

    private Buttons() {
    }

    /**
     * Setup the common properties for unlimited number of buttons and add them
     * to the specified panel.
     * 
     * @param panel
     * @param buttons
     */
    private static void configure(JPanel panel, JButton... buttons) {
	for (JButton button : buttons) {
	    button.setFont(new Font("Tahoma", Font.BOLD, 12));
	    panel.add(button);
	}
    }

    /**
     * Configures the buttons for panel: MAIN.
     * 
     * @param panelMain
     * @param button
     */
    static void configureMainButtons(JPanel panelMain, JButton profilesButton, JButton historiesButton,
	    JButton exitButton, JButton submitButton) {
	configure(panelMain, profilesButton, historiesButton, exitButton, submitButton);

	profilesButton.setBounds(25, 298, 184, 61);
	profilesButton.setToolTipText("Transfer to profiles view ( RESTRICTED ACCESS ) ");

	historiesButton.setBounds(231, 299, 233, 61);
	historiesButton.setToolTipText("Transfer to histories view ( RESTRICTED ACCESS ) ");

	exitButton.setBounds(231, 201, 232, 61);
	exitButton.setToolTipText("Exits the client application immediately");

	submitButton.setBounds(25, 201, 184, 61);
	exitButton.setToolTipText("Forwards and exectues your order");
    }

    /**
     * Configures the buttons for panel: PROFILES.
     * 
     * @param panelProfiles
     * @param logoutButtonProfiles
     * @param backButtonProfiles
     * @param personalProfileButton
     * @param genericProfileButton
     */
    static void configureProfilesButtons(JPanel panelProfiles, JButton logoutButtonProfiles,
	    JButton backButtonProfiles, JButton personalProfileButton, JButton genericProfileButton) {
	configure(panelProfiles, logoutButtonProfiles, backButtonProfiles, personalProfileButton, genericProfileButton);

	logoutButtonProfiles.setBounds(234, 282, 223, 64);
	logoutButtonProfiles.setToolTipText("Logs out of admin account");

	backButtonProfiles.setBounds(33, 283, 187, 64);
	backButtonProfiles.setToolTipText("Returns back to the main form");

	personalProfileButton.setBounds(33, 182, 147, 39);
	personalProfileButton.setToolTipText("Collects and outputs orders and profits from SPECIFIED client");

	genericProfileButton.setBounds(33, 70, 147, 39);
	genericProfileButton.setToolTipText("Collects and outputs orders and profits for ALL clients");
    }

    /**
     * Configures the buttons for panel: HISTORIES.
     * 
     * @param panelHistories
     * @param logoutButtonHistories
     * @param backButtonHistories
     * @param personalHistoryButton
     * @param genericHistoryButton
     */
    static void configureHistoriesButtons(JPanel panelHistories, JButton logoutButtonHistories,
	    JButton backButtonHistories, JButton personalHistoryButton, JButton genericHistoryButton) {
	configure(panelHistories, backButtonHistories, logoutButtonHistories, personalHistoryButton,
		genericHistoryButton);

	logoutButtonHistories.setBounds(237, 281, 223, 64);
	logoutButtonHistories.setToolTipText("Logs out of admin account");

	backButtonHistories.setBounds(33, 283, 187, 63);
	backButtonHistories.setToolTipText("Returns back to the main form");

	personalHistoryButton.setBounds(319, 184, 141, 55);
	personalHistoryButton.setToolTipText("Collects and outputs discount history for SPECIFIED client");

	genericHistoryButton.setBounds(319, 39, 141, 55);
	genericHistoryButton.setToolTipText("Collects and outputs discount history for ALL clients");
    }

    /**
     * Configures the buttons for panel: AUTHENTICATION.
     * 
     * @param panelAuthentication
     * @param loginButton
     * @param backButtonHistories
     * @param personalHistoryButton
     * @param genericHistoryButton
     */
    static void configureAuthenticationButtons(JPanel panelAuthentication, JButton loginButton,
	    JButton backButtonAuthentication) {
	configure(panelAuthentication, backButtonAuthentication, loginButton);

	loginButton.setBounds(71, 242, 164, 66);
	loginButton.setToolTipText("Logs in and provides access to RESTRICTED panels");

	backButtonAuthentication.setBounds(261, 242, 176, 66);
	backButtonAuthentication.setToolTipText("Returns back to the main form");
    }

}
