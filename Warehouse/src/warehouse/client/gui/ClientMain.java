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

    private JFrame clientForm;
    private JTextField usernameField;
    private JButton loginButton;
    private JButton backButtonProfiles;
    private JButton backButtonHistories;

    private static final String host = "localhost";
    private Socket clientConnection;

    private Connection dbConnection;
    private PreparedStatement request;
    private ResultSet response;

    private DataInputStream responseStream;
    private DataOutputStream requestStream;

    public static List<String> productsList;
    private int foundProduct = 0;
    private int loginDispatcher = 0;
    private boolean isLoggedIn = false;

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
		    window.clientForm.setVisible(true);
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
	clientForm = new JFrame();
	Forms.configureClientForm(clientForm);

	JPanel panelMain = new JPanel();
	JPanel panelAuthentication = new JPanel();
	JPanel panelProfiles = new JPanel();
	JPanel panelHistories = new JPanel();
	Panels.configurePanels(clientForm, panelMain, panelAuthentication, panelProfiles, panelHistories);

	// ---------------------- PANEL MAIN --------------------- //

	JLabel clienòLabel = new JLabel("Client ID:");
	JLabel productLabel = new JLabel("Product:");
	JLabel quantityLabel = new JLabel("Quantity:");
	JLabel sectionLabel = new JLabel("----------------------------EMPLOYEE SECTION----------------------------");
	JLabel outputLabel = new JLabel("YOUR ORDER:");
	Labels.configureMainLabels(panelMain, clienòLabel, productLabel, sectionLabel, quantityLabel, outputLabel);

	JTextField clientIdField = new JTextField();
	JTextField quantityField = new JTextField();
	JTextArea outputArea = new JTextArea();
	Areas.configureMainAreas(panelMain, outputArea);
	Fields.configureMainFields(panelMain, clientIdField, quantityField);

	JButton submitButton = new JButton("SUBMIT");
	submitButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		try {
		    if (foundProduct == 0) {
			JOptionPane.showMessageDialog(null, "Fill out order data first!", "ERROR",
				JOptionPane.ERROR_MESSAGE);
			return;
		    }

		    int client = Integer.parseInt(clientIdField.getText());
		    int product = foundProduct;
		    int quantity = Integer.parseInt(quantityField.getText());

		    sendOrderRequest(client, product, quantity);
		    showResponse();
		    submitButton.setEnabled(false);
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

		responseContent = responseStream.readUTF();
		outputArea.setText(responseContent);

		if (responseContent.contains("STOCK") || responseContent.contains("INSUFFICIENT")) {
		    outputArea.setBorder(BorderFactory.createLineBorder(Color.RED, 5, true));
		} else {
		    outputArea.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5, true));
		}
	    }

	    private void sendOrderRequest(int client, int product, int quantity) throws IOException {
		requestStream.writeInt(client);
		requestStream.writeInt(product);
		requestStream.writeInt(quantity);
	    }
	});

	JButton exitButton = new JButton("EXIT");
	exitButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		clientForm.dispose();
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

	JButton profilesButton = new JButton("ORDERS & PROFITS");
	profilesButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (isLoggedIn) {
		    panelMain.setVisible(false);
		    panelProfiles.setVisible(true);
		    clientForm.getRootPane().setDefaultButton(backButtonProfiles);
		} else {
		    panelMain.setVisible(false);
		    panelAuthentication.setVisible(true);
		    loginDispatcher = LOG_IN_PROFILES;
		    usernameField.requestFocusInWindow();
		    clientForm.getRootPane().setDefaultButton(loginButton);
		}
	    }
	});

	JButton historiesButton = new JButton("DISCOUNTS HISTORY");
	historiesButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (isLoggedIn) {
		    panelMain.setVisible(false);
		    panelHistories.setVisible(true);
		    clientForm.getRootPane().setDefaultButton(backButtonHistories);
		} else {
		    panelMain.setVisible(false);
		    panelAuthentication.setVisible(true);
		    loginDispatcher = LOG_IN_HISTORIES;
		    usernameField.requestFocusInWindow();
		    clientForm.getRootPane().setDefaultButton(loginButton);
		}
	    }
	});

	JComboBox<Object> productChoiceList = new JComboBox<>(productsList.toArray());
	productChoiceList.setSelectedItem(null);
	productChoiceList.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		try {
		    String productInformation = (String) productChoiceList.getSelectedItem();
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

		request = dbConnection.prepareStatement(query);
		response = request.executeQuery();

		while (response.next()) {
		    foundProduct = response.getInt(1);
		}
	    }
	});
	ChoiceLists.configureMainProductsList(panelMain, productChoiceList);
	Buttons.configureMainButtons(panelMain, profilesButton, historiesButton, exitButton, submitButton);
	clientForm.getRootPane().setDefaultButton(submitButton);

	// ---------------------- PANEL PROFILES --------------------- //

	JLabel clientLabelProfiles = new JLabel("CLIENT ID:");
	JLabel genericProfileLabel = new JLabel("PROFILE OF ALL ORDERS");
	JLabel personalProfileLabel = new JLabel("PROFILE OF SPECIFIC CLIENT:");
	JLabel outputProfilesLabel = new JLabel("ORDER & CLIENT PROFILES");
	Labels.configureProfilesLabels(panelProfiles, outputProfilesLabel, personalProfileLabel, genericProfileLabel,
		clientLabelProfiles);

	JTextField clientFieldProfiles = new JTextField();
	Fields.configureProfilesFields(panelProfiles, clientFieldProfiles);

	JTextArea genericProfileArea = new JTextArea();
	JTextArea personalProfileArea = new JTextArea();
	Areas.configureProfilesAreas(panelProfiles, personalProfileArea, genericProfileArea);

	JButton genericProfileButton = new JButton("ORDERS PROFILE");
	genericProfileButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		String profile = StringConstants.EMPTY;
		try {
		    profile = assemleProfile();
		    showProfile(genericProfileArea, profile);
		} catch (SQLException sqle) {
		    LOG.error("Problem accessing DB: ", sqle);
		    JOptionPane.showMessageDialog(null, "Problem accessing DB: " + sqle.getLocalizedMessage(), "ERROR",
			    JOptionPane.ERROR_MESSAGE);
		}
	    }

	    private String assemleProfile() throws SQLException {
		String profile = StringConstants.EMPTY;
		request = dbConnection.prepareStatement("call ordersProfile;");
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

	JButton personalProfileButton = new JButton("CLIENT PROFILE");
	personalProfileButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		String profile = StringConstants.EMPTY;
		int clientId = 0;

		try {
		    clientId = Integer.parseInt(clientFieldProfiles.getText());
		    profile = assembleClientProfile(clientId);
		    showClientProfile(personalProfileArea, profile);
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

		request = dbConnection.prepareStatement("call clientProfile(" + clientId + ");");
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

	backButtonProfiles = new JButton("BACK TO MAIN");
	backButtonProfiles.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		panelMain.setVisible(true);
		panelProfiles.setVisible(false);
		clientForm.getRootPane().setDefaultButton(submitButton);
	    }
	});

	JButton logoutButtonProfiles = new JButton("LOGOUT");
	logoutButtonProfiles.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		panelProfiles.setVisible(false);
		panelMain.setVisible(true);
		isLoggedIn = false;
		clientForm.getRootPane().setDefaultButton(submitButton);
	    }
	});
	Buttons.configureProfilesButtons(panelProfiles, logoutButtonProfiles, backButtonProfiles,
		personalProfileButton, genericProfileButton);

	// ---------------------- PANEL HISTORIES --------------------- //

	JLabel dicountsLabel = new JLabel("DISCOUNTS HISTORY");
	JLabel clientIdLabelDiscounts = new JLabel("CLIENT NAME:");
	Labels.configureHistoriesLabels(panelHistories, clientIdLabelDiscounts, dicountsLabel);

	JTextField clientNameField = new JTextField();
	JTextArea discountsHistoryArea = new JTextArea();
	Fields.configureHistoriesFields(panelHistories, clientNameField);
	Areas.configureHistoriesAreas(panelHistories, discountsHistoryArea);

	JButton genericHistoryButton = new JButton("VIEW ALL");
	genericHistoryButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		String report = StringConstants.EMPTY;

		try {

		    report = assembleDiscountReport();
		    showDiscountReport(discountsHistoryArea, report);
		} catch (SQLException sqle) {
		    LOG.error("Problem accessing DB: ", sqle);
		    JOptionPane.showMessageDialog(null, "Problem accessing DB: " + sqle.getLocalizedMessage(), "ERROR",
			    JOptionPane.ERROR_MESSAGE);
		}

	    }

	    private String assembleDiscountReport() throws SQLException {
		String report = StringConstants.EMPTY;
		String reportEntry = StringConstants.EMPTY;

		request = dbConnection.prepareStatement("select *from discountHistory;");
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

	JButton personalHistoryButton = new JButton("VIEW PER CLIENT");
	personalHistoryButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		String clientDiscountReport = StringConstants.EMPTY;

		try {
		    clientDiscountReport = assembleClientDiscountReport();
		    showClientDiscountReport(discountsHistoryArea, clientDiscountReport);
		} catch (SQLException sqle) {
		    LOG.error("Problem accessing DB: ", sqle);
		    JOptionPane.showMessageDialog(null, "Problem accessing DB: " + sqle.getLocalizedMessage(), "ERROR",
			    JOptionPane.ERROR_MESSAGE);
		}
	    }

	    private String assembleClientDiscountReport() throws SQLException {
		String report = StringConstants.EMPTY;
		String reportEntry = StringConstants.EMPTY;
		String clientName = clientNameField.getText();

		if (QueryValidator.validateClientNameString(clientName)) {
		    request = dbConnection.prepareStatement("select *from discountHistory where client="
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

	backButtonHistories = new JButton("BACK TO MAIN");
	backButtonHistories.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		panelMain.setVisible(true);
		panelHistories.setVisible(false);
		clientForm.getRootPane().setDefaultButton(submitButton);
	    }
	});

	JButton logoutButtonHistories = new JButton("LOGOUT");
	logoutButtonHistories.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		panelHistories.setVisible(false);
		panelMain.setVisible(true);
		isLoggedIn = false;
		clientForm.getRootPane().setDefaultButton(submitButton);
	    }
	});
	Buttons.configureHistoriesButtons(panelHistories, logoutButtonHistories, backButtonHistories,
		personalHistoryButton, genericHistoryButton);

	// ---------------------- PANEL AUTHENTICATION ---------------------//

	JLabel loginLabel = new JLabel("AUTHENTICATION FORM");
	JLabel usernameLabel = new JLabel("USERNAME:");
	JLabel passwordLabel = new JLabel("PASSWORD:");
	Labels.configureAuthenticationLabels(panelAuthentication, passwordLabel, usernameLabel, loginLabel);

	usernameField = new JTextField();
	JPasswordField passwordField = new JPasswordField();
	Fields.configureAuthenticationFields(panelAuthentication, usernameField, passwordField);

	loginButton = new JButton("LOG IN");
	loginButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		String username = usernameField.getText();
		String password = new String(passwordField.getPassword());

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
			usernameLabel.setForeground(Color.RED);
			usernameField.setBackground(Color.RED);
			passwordLabel.setForeground(Color.RED);
			passwordField.setBackground(Color.RED);
		    } else if (!username.equalsIgnoreCase(DBConstants.USER)) {
			usernameLabel.setForeground(Color.RED);
			usernameField.setBackground(Color.RED);
		    } else if (!password.equals(DBConstants.PASSWORD)) {
			passwordLabel.setForeground(Color.RED);
			passwordField.setBackground(Color.RED);
		    }
		    JOptionPane.showMessageDialog(null, "Wrong username/password!!!", "ERROR",
			    JOptionPane.ERROR_MESSAGE);
		}
	    }

	    private void clearFields() {
		passwordField.setText(StringConstants.EMPTY);
		usernameField.setText(StringConstants.EMPTY);
		usernameField.setBackground(Color.WHITE);
		passwordField.setBackground(Color.WHITE);
		usernameLabel.setForeground(Color.YELLOW);
		passwordLabel.setForeground(Color.YELLOW);
	    }

	    private void proceedToPanel(JPanel currentPannel, JPanel nextPanel) {
		currentPannel.setVisible(false);
		nextPanel.setVisible(true);
		isLoggedIn = true;
		if (loginDispatcher == LOG_IN_PROFILES) {
		    clientForm.getRootPane().setDefaultButton(backButtonProfiles);
		} else {
		    clientForm.getRootPane().setDefaultButton(backButtonHistories);
		}
	    }
	});

	JButton backButtonAuthentication = new JButton("BACK TO MAIN");
	backButtonAuthentication.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		panelMain.setVisible(true);
		panelAuthentication.setVisible(false);
		clientForm.getRootPane().setDefaultButton(submitButton);

		passwordField.setText(StringConstants.EMPTY);
		usernameField.setText(StringConstants.EMPTY);
		usernameField.setBackground(Color.WHITE);
		passwordField.setBackground(Color.WHITE);
		usernameLabel.setForeground(Color.YELLOW);
		passwordLabel.setForeground(Color.YELLOW);
	    }
	});
	Buttons.configureAuthenticationButtons(panelAuthentication, loginButton, backButtonAuthentication);
    }

    private void initResources() throws UnknownHostException, IOException, ClassNotFoundException, SQLException {
	clientConnection = new Socket(host, WarehouseServer.PORT);
	dbConnection = DatabaseConnector.getInstance().getConnection();
	responseStream = new DataInputStream(clientConnection.getInputStream());
	requestStream = new DataOutputStream(clientConnection.getOutputStream());
    }

    private void initComboBox() throws Exception {
	String query = "select `group`,`brand`,`model` from products order by `group`";

	request = dbConnection.prepareStatement(query);
	response = request.executeQuery();
	productsList = new ArrayList<String>();

	while (response.next()) {
	    productsList.add(response.getString("group") + StringConstants.SPACE + response.getString("brand")
		    + StringConstants.SPACE + response.getString("model"));
	}
    }

    private void cleanup() throws IOException, SQLException {
	requestStream.close();
	responseStream.close();
    }

}