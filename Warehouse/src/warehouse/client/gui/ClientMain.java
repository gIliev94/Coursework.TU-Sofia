package warehouse.client.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.log4j.Logger;
import warehouse.database.DBConstants;
import warehouse.database.DatabaseConnector;
import warehouse.database.QueryValidator;
import warehouse.server.WarehouseServer;
import warehouse.utilities.StringConstants;

/**
 * Client logic implementation/GUI for interaction with the server.
 * 
 * @author Georgi Iliev
 *
 */
public class ClientMain {

    private static final Logger LOG = Logger.getLogger(ClientMain.class);

    private JFrame formClient;
    private JTextField fieldUsername;
    private JButton buttonLogin;
    private JButton buttonBackProfiles;
    private JButton buttonBackHistories;

    private static final String host = "localhost";
    private Socket connectionClient;

    private Connection connectionDB;
    private PreparedStatement request;
    private ResultSet response;

    private DataInputStream streamResponse;
    private DataOutputStream streamRequest;

    public static List<String> productsList;
    private boolean userLoggedIn = false;
    private int foundProduct = 0;
    private int loginDispatcher = 0;

    private static final int LOG_IN_PROFILES = 1;
    private static final int LOG_IN_HISTORIES = 2;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		try {
		    ClientMain window = new ClientMain();
		    window.formClient.setVisible(true);
		} catch (ConnectException ce) {
		    LOG.info("No server available for connection!");
		    JOptionPane.showMessageDialog(null, "No server available for connection!", "ERROR",
			    JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
		    LOG.error("Problem occurred: ", e);
		    JOptionPane.showMessageDialog(null, "Problem occurred: " + e.getLocalizedMessage(), "ERROR",
			    JOptionPane.ERROR_MESSAGE);
		}

	    }
	});
    }

    /**
     * Create the application.
     * 
     * @throws Exception
     */
    public ClientMain() throws Exception {
	initResources();
	initComboBox();
	setComponentStyles();
	initialize();
    }

    /**
     * Sets the look and feel styles of all visual components to Nimbus style or
     * Windows classic if the former is not installed.
     * 
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws UnsupportedLookAndFeelException
     */
    private void setComponentStyles() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
	    UnsupportedLookAndFeelException {
	boolean found = false;
	UIManager.LookAndFeelInfo iLAFs[] = UIManager.getInstalledLookAndFeels();

	for (int i = 0; i < iLAFs.length; i++) {
	    if (iLAFs[i].getName().equals("Nimbus")) {
		UIManager.setLookAndFeel(iLAFs[i].getClassName());
		found = true;
	    }
	}

	if (!found) {
	    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
	}
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
	formClient = new JFrame();
	Forms.configureClientForm(formClient);

	JPanel panelMain = new JPanel();
	JPanel panelAuthentication = new JPanel();
	JPanel panelProfiles = new JPanel();
	JPanel panelHistories = new JPanel();
	Panels.configurePanels(formClient, panelMain, panelAuthentication, panelProfiles, panelHistories);

	// ---------------------- PANEL MAIN --------------------- //

	JLabel labelClient = new JLabel("Client ID:");
	JLabel labelProduct = new JLabel("Product:");
	JLabel labelQuantity = new JLabel("Quantity:");
	JLabel labelSection = new JLabel("----------------------------EMPLOYEE SECTION----------------------------");
	JLabel labelOutput = new JLabel("YOUR ORDER:");
	Labels.configureMainLabels(panelMain, labelClient, labelProduct, labelSection, labelQuantity, labelOutput);

	JTextField fieldClientId = new JTextField();
	JTextField fieldQuantity = new JTextField();
	JTextArea areaOutput = new JTextArea();
	Areas.configureMainAreas(panelMain, areaOutput);
	Fields.configureMainFields(panelMain, fieldClientId, fieldQuantity);

	JButton buttonSubmit = new JButton("SUBMIT");
	buttonSubmit.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		try {
		    if (foundProduct == 0) {
			JOptionPane.showMessageDialog(null, "Fill out order data first!", "ERROR",
				JOptionPane.ERROR_MESSAGE);
			return;
		    }

		    int client = Integer.parseInt(fieldClientId.getText());
		    int product = foundProduct;
		    int quantity = Integer.parseInt(fieldQuantity.getText());

		    sendOrderRequest(client, product, quantity);
		    showResponse();
		    buttonSubmit.setEnabled(false);
		} catch (IOException ioe) {
		    LOG.warn("Problem exchanging information: ", ioe);
		    JOptionPane.showMessageDialog(null, "Problem exchanging information: " + ioe.getLocalizedMessage(),
			    "ERROR", JOptionPane.ERROR_MESSAGE);
		} catch (NumberFormatException nfe) {
		    LOG.error("Illegal parameter of query: ", nfe);
		    JOptionPane.showMessageDialog(null, "Illegal  ID / quantity - input POSITIVE, WHOLE numbers only!",
			    "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	    }

	    private void showResponse() throws IOException {
		String responseContent = StringConstants.EMPTY;

		responseContent = streamResponse.readUTF();
		areaOutput.setText(responseContent);

		if (responseContent.contains("STOCK") || responseContent.contains("INSUFFICIENT")) {
		    areaOutput.setBorder(BorderFactory.createLineBorder(Color.RED, 5, true));
		} else {
		    areaOutput.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5, true));
		}
	    }

	    private void sendOrderRequest(int client, int product, int quantity) throws IOException {
		streamRequest.writeInt(client);
		streamRequest.writeInt(product);
		streamRequest.writeInt(quantity);
	    }
	});

	JButton buttonExit = new JButton("EXIT");
	buttonExit.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		formClient.dispose();
		try {
		    cleanup();
		} catch (IOException e) {
		    LOG.error("Problem occurred: ", e);
		    JOptionPane.showMessageDialog(null, "Problem occurred: " + e.getLocalizedMessage(), "ERROR",
			    JOptionPane.ERROR_MESSAGE);
		} catch (SQLException sqle) {
		    LOG.error("Problem accessing DB: ", sqle);
		    JOptionPane.showMessageDialog(null, "Problem accessing DB: " + sqle.getLocalizedMessage(), "ERROR",
			    JOptionPane.ERROR_MESSAGE);
		}
		JOptionPane.showMessageDialog(null, "You have disconnected!", "INFO", JOptionPane.INFORMATION_MESSAGE);
	    }
	});

	JButton buttonProfiles = new JButton("ORDERS & PROFITS");
	buttonProfiles.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (userLoggedIn) {
		    panelMain.setVisible(false);
		    panelProfiles.setVisible(true);
		    formClient.getRootPane().setDefaultButton(buttonBackProfiles);
		} else {
		    panelMain.setVisible(false);
		    panelAuthentication.setVisible(true);
		    loginDispatcher = LOG_IN_PROFILES;
		    fieldUsername.requestFocusInWindow();
		    formClient.getRootPane().setDefaultButton(buttonLogin);
		}
	    }
	});

	JButton buttonHistories = new JButton("DISCOUNTS HISTORY");
	buttonHistories.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (userLoggedIn) {
		    panelMain.setVisible(false);
		    panelHistories.setVisible(true);
		    formClient.getRootPane().setDefaultButton(buttonBackHistories);
		} else {
		    panelMain.setVisible(false);
		    panelAuthentication.setVisible(true);
		    loginDispatcher = LOG_IN_HISTORIES;
		    fieldUsername.requestFocusInWindow();
		    formClient.getRootPane().setDefaultButton(buttonLogin);
		}
	    }
	});

	JComboBox<Object> choiceListProduct = new JComboBox<>(productsList.toArray());
	choiceListProduct.setSelectedItem(null);
	choiceListProduct.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		try {
		    String productInformation = (String) choiceListProduct.getSelectedItem();
		    String model = productInformation.substring(productInformation.length() - 3);

		    findProduct(model);
		} catch (SQLException sqle) {
		    LOG.error("Problem accessing DB: ", sqle);
		    JOptionPane.showMessageDialog(null, "Problem accessing DB: " + sqle.getLocalizedMessage(), "ERROR",
			    JOptionPane.ERROR_MESSAGE);
		}
	    }

	    private void findProduct(String model) throws SQLException {
		String query = "select `id` from products where `model` like " + StringConstants.quote("%" + model);

		request = connectionDB.prepareStatement(query);
		response = request.executeQuery();

		while (response.next()) {
		    foundProduct = response.getInt(1);
		}
	    }
	});
	ChoiceLists.configureMainProductsList(panelMain, choiceListProduct);
	Buttons.configureMainButtons(panelMain, buttonProfiles, buttonHistories, buttonExit, buttonSubmit);
	formClient.getRootPane().setDefaultButton(buttonSubmit);

	// ---------------------- PANEL PROFILES --------------------- //

	JLabel labelClientProfiles = new JLabel("CLIENT ID:");
	JLabel labelGenericProfile = new JLabel("PROFILE OF ALL ORDERS");
	JLabel labelPersonalProfile = new JLabel("PROFILE OF SPECIFIC CLIENT:");
	JLabel labelOutputProfiles = new JLabel("ORDER & CLIENT PROFILES");
	Labels.configureProfilesLabels(panelProfiles, labelOutputProfiles, labelPersonalProfile, labelGenericProfile,
		labelClientProfiles);

	JTextField fieldClientProfiles = new JTextField();
	Fields.configureProfilesFields(panelProfiles, fieldClientProfiles);

	JTextArea areaGenericProfile = new JTextArea();
	JTextArea areaPersonalProfile = new JTextArea();
	Areas.configureProfilesAreas(panelProfiles, areaPersonalProfile, areaGenericProfile);

	JButton buttonGenericProfile = new JButton("ORDERS PROFILE");
	buttonGenericProfile.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		String profile = StringConstants.EMPTY;
		try {
		    profile = assemleProfile();
		    showProfile(areaGenericProfile, profile);
		} catch (SQLException sqle) {
		    LOG.error("Problem accessing DB: ", sqle);
		    JOptionPane.showMessageDialog(null, "Problem accessing DB: " + sqle.getLocalizedMessage(), "ERROR",
			    JOptionPane.ERROR_MESSAGE);
		}
	    }

	    private String assemleProfile() throws SQLException {
		String profile = StringConstants.EMPTY;
		request = connectionDB.prepareStatement("call ordersProfile;");
		response = request.executeQuery();
		while (response.next()) {
		    profile = response.getString("ordersMade") + StringConstants.TAB
			    + response.getString("itemsOrdered") + StringConstants.TAB
			    + response.getString("totalMonetaryProfits");
		}

		return profile;
	    }

	    private void showProfile(JTextArea profileOfAllTextArea, String profile) {
		profile = "ORDERS" + StringConstants.TAB + "ITEMS" + StringConstants.TAB + "PROFITS"
			+ StringConstants.LINE + profile;
		profileOfAllTextArea.setText(profile);
	    }

	});

	JButton buttonPersonalProfile = new JButton("CLIENT PROFILE");
	buttonPersonalProfile.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		String profile = StringConstants.EMPTY;
		int clientId = 0;

		try {
		    clientId = Integer.parseInt(fieldClientProfiles.getText());
		    profile = assembleClientProfile(clientId);
		    showClientProfile(areaPersonalProfile, profile);
		} catch (SQLException sqle) {
		    LOG.error("Problem accessing DB: ", sqle);
		    JOptionPane.showMessageDialog(null, "Problem accessing DB: " + sqle.getLocalizedMessage(), "ERROR",
			    JOptionPane.ERROR_MESSAGE);
		} catch (NumberFormatException nfe) {
		    LOG.error("Illegal parameter of query: ", nfe);
		    JOptionPane.showMessageDialog(null, "Illegal ID - input POSITIVE, WHOLE numbers only!", "ERROR",
			    JOptionPane.ERROR_MESSAGE);
		}
	    }

	    private String assembleClientProfile(int clientId) throws SQLException, NumberFormatException {
		String clientProfile = StringConstants.EMPTY;

		request = connectionDB.prepareStatement("call clientProfile(" + clientId + ");");
		response = request.executeQuery();

		while (response.next()) {
		    clientProfile = response.getString("ordersMade") + StringConstants.TAB
			    + response.getString("itemsOrdered") + StringConstants.TAB
			    + response.getString("totalMonetaryProfits");
		}

		return clientProfile;
	    }

	    private void showClientProfile(JTextArea profileClientTextArea, String profile) {
		profile = "ORDERS" + StringConstants.TAB + "ITEMS" + StringConstants.TAB + "PROFITS"
			+ StringConstants.LINE + profile;
		profileClientTextArea.setText(profile);
	    }
	});

	buttonBackProfiles = new JButton("BACK TO MAIN");
	buttonBackProfiles.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		panelMain.setVisible(true);
		panelProfiles.setVisible(false);
		formClient.getRootPane().setDefaultButton(buttonSubmit);
	    }
	});

	JButton buttonLogoutProfiles = new JButton("LOGOUT");
	buttonLogoutProfiles.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		panelProfiles.setVisible(false);
		panelMain.setVisible(true);
		userLoggedIn = false;
		formClient.getRootPane().setDefaultButton(buttonSubmit);
	    }
	});
	Buttons.configureProfilesButtons(panelProfiles, buttonLogoutProfiles, buttonBackProfiles,
		buttonPersonalProfile, buttonGenericProfile);

	// ---------------------- PANEL HISTORIES --------------------- //

	JLabel labelDiscounts = new JLabel("DISCOUNTS HISTORY");
	JLabel labelClientIdDiscounts = new JLabel("CLIENT NAME:");
	Labels.configureHistoriesLabels(panelHistories, labelClientIdDiscounts, labelDiscounts);

	JTextField fieldClientName = new JTextField();
	JTextArea areaDiscountHistory = new JTextArea();
	Fields.configureHistoriesFields(panelHistories, fieldClientName);
	Areas.configureHistoriesAreas(panelHistories, areaDiscountHistory);

	JButton buttonGenericHistory = new JButton("VIEW ALL");
	buttonGenericHistory.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		String report = StringConstants.EMPTY;

		try {

		    report = assembleDiscountReport();
		    showDiscountReport(areaDiscountHistory, report);
		} catch (SQLException sqle) {
		    LOG.error("Problem accessing DB: ", sqle);
		    JOptionPane.showMessageDialog(null, "Problem accessing DB: " + sqle.getLocalizedMessage(), "ERROR",
			    JOptionPane.ERROR_MESSAGE);
		}

	    }

	    private String assembleDiscountReport() throws SQLException {
		String report = StringConstants.EMPTY;
		String reportEntry = StringConstants.EMPTY;

		request = connectionDB.prepareStatement("select *from discountHistory;");
		response = request.executeQuery();

		while (response.next()) {
		    reportEntry = StringConstants.SPACE + response.getString("client") + "             "
			    + response.getString("discount");
		    report = report + StringConstants.LINE + reportEntry;
		}

		return report;
	    }

	    private void showDiscountReport(JTextArea discountsHistoryTextArea, String report) {
		String captions = StringConstants.EMPTY;
		captions = " Client:" + StringConstants.TAB + "         Discount:" + StringConstants.LINE;
		discountsHistoryTextArea.setText(captions + report);
	    }
	});

	JButton buttonPersonalHistory = new JButton("VIEW PER CLIENT");
	buttonPersonalHistory.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		String clientDiscountReport = StringConstants.EMPTY;

		try {
		    clientDiscountReport = assembleClientDiscountReport();
		    showClientDiscountReport(areaDiscountHistory, clientDiscountReport);
		} catch (SQLException sqle) {
		    LOG.error("Problem accessing DB: ", sqle);
		    JOptionPane.showMessageDialog(null, "Problem accessing DB: " + sqle.getLocalizedMessage(), "ERROR",
			    JOptionPane.ERROR_MESSAGE);
		}
	    }

	    private String assembleClientDiscountReport() throws SQLException {
		String report = StringConstants.EMPTY;
		String reportEntry = StringConstants.EMPTY;
		String clientName = fieldClientName.getText();

		if (QueryValidator.validateClientNameString(clientName)) {
		    request = connectionDB.prepareStatement("select *from discountHistory where client="
			    + StringConstants.quote(clientName) + "order by discount;");
		    response = request.executeQuery();
		}

		while (response.next()) {
		    reportEntry = StringConstants.SPACE + response.getString("client") + "             "
			    + response.getString("discount");
		    report = report + StringConstants.LINE + reportEntry;
		}

		return report != StringConstants.EMPTY ? report : "\nWrong input - nothing to display";
	    }

	    private void showClientDiscountReport(JTextArea discountsHistoryTextArea, String clientDiscountReport) {
		String captions = StringConstants.EMPTY;
		captions = " Client:" + StringConstants.TAB + "         Discount:" + StringConstants.LINE;
		discountsHistoryTextArea.setText(captions + clientDiscountReport);
	    }

	});

	buttonBackHistories = new JButton("BACK TO MAIN");
	buttonBackHistories.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		panelMain.setVisible(true);
		panelHistories.setVisible(false);
		formClient.getRootPane().setDefaultButton(buttonSubmit);
	    }
	});

	JButton buttonLogoutHistories = new JButton("LOGOUT");
	buttonLogoutHistories.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		panelHistories.setVisible(false);
		panelMain.setVisible(true);
		userLoggedIn = false;
		formClient.getRootPane().setDefaultButton(buttonSubmit);
	    }
	});
	Buttons.configureHistoriesButtons(panelHistories, buttonLogoutHistories, buttonBackHistories,
		buttonPersonalHistory, buttonGenericHistory);

	// ---------------------- PANEL AUTHENTICATION ---------------------//

	JLabel labelLogin = new JLabel("AUTHENTICATION FORM");
	JLabel labelUsername = new JLabel("USERNAME:");
	JLabel labelPassword = new JLabel("PASSWORD:");
	Labels.configureAuthenticationLabels(panelAuthentication, labelPassword, labelUsername, labelLogin);

	fieldUsername = new JTextField();
	JPasswordField fieldPassword = new JPasswordField();
	Fields.configureAuthenticationFields(panelAuthentication, fieldUsername, fieldPassword);

	buttonLogin = new JButton("LOG IN");
	buttonLogin.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		String username = fieldUsername.getText();
		String password = new String(fieldPassword.getPassword());

		if (username.equalsIgnoreCase(DBConstants.USER) && password.equals(DBConstants.PASSWORD)
			&& loginDispatcher == LOG_IN_PROFILES) {
		    proceedToPanel(panelAuthentication, panelProfiles);
		    clearFields();
		} else if (username.equalsIgnoreCase(DBConstants.USER) && password.equals(DBConstants.PASSWORD)
			&& loginDispatcher == LOG_IN_HISTORIES) {
		    proceedToPanel(panelAuthentication, panelHistories);
		    clearFields();
		} else {
		    if (!username.equalsIgnoreCase(DBConstants.USER) && !password.equals(DBConstants.PASSWORD)) {
			labelUsername.setForeground(Color.RED);
			fieldUsername.setBackground(Color.RED);
			labelPassword.setForeground(Color.RED);
			fieldPassword.setBackground(Color.RED);
		    } else if (!username.equalsIgnoreCase(DBConstants.USER)) {
			labelUsername.setForeground(Color.RED);
			fieldUsername.setBackground(Color.RED);
		    } else if (!password.equals(DBConstants.PASSWORD)) {
			labelPassword.setForeground(Color.RED);
			fieldPassword.setBackground(Color.RED);
		    }
		    JOptionPane.showMessageDialog(null, "Wrong username/password!!!", "ERROR",
			    JOptionPane.ERROR_MESSAGE);
		}
	    }

	    private void clearFields() {
		fieldPassword.setText(StringConstants.EMPTY);
		fieldUsername.setText(StringConstants.EMPTY);
		fieldUsername.setBackground(Color.WHITE);
		fieldPassword.setBackground(Color.WHITE);
		labelUsername.setForeground(Color.YELLOW);
		labelPassword.setForeground(Color.YELLOW);
	    }

	    private void proceedToPanel(JPanel currentPannel, JPanel nextPanel) {
		currentPannel.setVisible(false);
		nextPanel.setVisible(true);
		userLoggedIn = true;
		if (loginDispatcher == LOG_IN_PROFILES) {
		    formClient.getRootPane().setDefaultButton(buttonBackProfiles);
		} else {
		    formClient.getRootPane().setDefaultButton(buttonBackHistories);
		}
	    }
	});

	JButton buttonBackAuthentication = new JButton("BACK TO MAIN");
	buttonBackAuthentication.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		panelMain.setVisible(true);
		panelAuthentication.setVisible(false);
		formClient.getRootPane().setDefaultButton(buttonSubmit);

		fieldPassword.setText(StringConstants.EMPTY);
		fieldUsername.setText(StringConstants.EMPTY);
		fieldUsername.setBackground(Color.WHITE);
		fieldPassword.setBackground(Color.WHITE);
		labelUsername.setForeground(Color.YELLOW);
		labelPassword.setForeground(Color.YELLOW);
	    }
	});
	Buttons.configureAuthenticationButtons(panelAuthentication, buttonLogin, buttonBackAuthentication);
    }

    private void initResources() throws UnknownHostException, IOException, ClassNotFoundException, SQLException {
	connectionClient = new Socket(host, WarehouseServer.PORT);
	connectionDB = DatabaseConnector.getInstance().getConnection();
	streamResponse = new DataInputStream(connectionClient.getInputStream());
	streamRequest = new DataOutputStream(connectionClient.getOutputStream());
    }

    private void initComboBox() throws Exception {
	String query = "select `group`,`brand`,`model` from products order by `group`";

	request = connectionDB.prepareStatement(query);
	response = request.executeQuery();
	productsList = new ArrayList<String>();

	while (response.next()) {
	    productsList.add(response.getString("group") + StringConstants.SPACE + response.getString("brand")
		    + StringConstants.SPACE + response.getString("model"));
	}
    }

    private void cleanup() throws IOException, SQLException {
	streamRequest.close();
	streamResponse.close();
    }

}