package warehouse.client;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
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

import javax.swing.ImageIcon;
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
import warehouse.server.WarehouseServer;

/**
 * Client logic implementation/GUI for interaction with the server.
 * 
 * @author Georgi Iliev
 *
 */
public class Client {

    private static final Logger LOG = Logger.getLogger(Client.class);

    private JFrame formClientRequest;
    private JTextArea responseTextArea;
    private JLabel orderCaptionLabel;
    private JTextField clientIdTextField;
    private JTextField quantityTextField;
    private JTextField clientIdTextFieldOrders;
    private JTextField clientIdTextFieldDiscounts;
    private JPasswordField passwordTextField;
    private JTextField usernameTextField;
    private JButton loginButton;
    private JButton backButtonOrders;
    private JButton backButtonDiscounts;

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
		    setComponentStyles();
		    Client window = new Client();
		    window.formClientRequest.setVisible(true);
		} catch (ConnectException ce) {
		    LOG.info("No server available for connection!");
		    JOptionPane.showMessageDialog(null, "No server available for connection!", "ERROR",
			    JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
		    LOG.error("Problem occurred: ", e);
		    JOptionPane.showMessageDialog(null, "Problem occurred: " + e.getClass().getName(), "ERROR",
			    JOptionPane.ERROR_MESSAGE);
		}

	    }

	    private void setComponentStyles() throws ClassNotFoundException, InstantiationException,
		    IllegalAccessException, UnsupportedLookAndFeelException {

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
	});
    }

    /**
     * Create the application.
     * 
     * @throws Exception
     */
    public Client() throws Exception {
	initResources();
	initComboBox();
	initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
	setupForm();

	JPanel panelMain = new JPanel();
	JPanel panelAuthentication = new JPanel();
	JPanel panelProfiles = new JPanel();
	JPanel panelHistories = new JPanel();
	setupPanels(panelMain, panelAuthentication, panelProfiles, panelHistories);

	// ---------------------- PANEL MAIN ---------------------//

	JLabel clientIdLabel = new JLabel("Client ID:");
	JLabel productIdLabel = new JLabel("Product:");
	JLabel quantityLabel = new JLabel("Quantity:");
	JLabel sectionLabel = new JLabel("----------------------------EMPLOYEE SECTION----------------------------");
	orderCaptionLabel = new JLabel("YOUR ORDER:");
	setupMainPanelLabels(panelMain, clientIdLabel, productIdLabel, sectionLabel, quantityLabel);

	clientIdTextField = new JTextField();
	quantityTextField = new JTextField();
	responseTextArea = new JTextArea();
	responseTextArea.setFocusable(false);
	setupMainPanelFieldsAndArea(panelMain);

	JButton submitButton = new JButton("SUBMIT");
	submitButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		try {
		    if (foundProduct == 0) {
			JOptionPane.showMessageDialog(null, "Fill out order data first!", "ERROR",
				JOptionPane.ERROR_MESSAGE);
			return;
		    }

		    int client = Integer.parseInt(clientIdTextField.getText());
		    int product = foundProduct;
		    int quantity = Integer.parseInt(quantityTextField.getText());

		    sendOrderRequest(client, product, quantity);
		    receiveResponse(submitButton);
		} catch (IOException ioe) {
		    LOG.warn("Problem exchanging information: ", ioe);
		    JOptionPane.showMessageDialog(null, "Problem exchanging information: " + ioe.getMessage(), "ERROR",
			    JOptionPane.ERROR_MESSAGE);
		}
	    }

	    private void receiveResponse(JButton submitButton) throws IOException {
		String responseContent;
		responseContent = responseStream.readUTF();
		responseTextArea.setText(responseContent);
		submitButton.setEnabled(false);
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
		formClientRequest.dispose();
		JOptionPane.showMessageDialog(null, "Client disconnected.", "INFO", JOptionPane.INFORMATION_MESSAGE);
	    }
	});

	JButton ordersProfilesButton = new JButton("ORDERS & PROFITS");
	ordersProfilesButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (isLoggedIn) {
		    panelMain.setVisible(false);
		    panelProfiles.setVisible(true);
		    formClientRequest.getRootPane().setDefaultButton(backButtonOrders);
		} else {
		    panelMain.setVisible(false);
		    panelAuthentication.setVisible(true);
		    loginDispatcher = LOG_IN_PROFILES;
		    usernameTextField.requestFocusInWindow();
		    formClientRequest.getRootPane().setDefaultButton(loginButton);
		}
	    }
	});

	JComboBox<Object> productsDropDownList = new JComboBox<>(productsList.toArray());
	productsDropDownList.setSelectedItem(null);
	productsDropDownList.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		try {
		    String productInformation = (String) productsDropDownList.getSelectedItem();
		    String model = productInformation.substring(productInformation.length() - 3);

		    findProduct(model);
		} catch (SQLException sqle) {
		    LOG.error("Problem accessing DB: ", sqle);
		    JOptionPane.showMessageDialog(null, "Problem accessing DB: " + sqle.getMessage(), "ERROR",
			    JOptionPane.ERROR_MESSAGE);
		}
	    }

	    private void findProduct(String model) throws SQLException {
		String query = "select `id` from products where `model` like " + "\"%" + model + "\"" + ";";
		request = dbConnection.prepareStatement(query);
		response = request.executeQuery();
		while (response.next()) {
		    foundProduct = response.getInt(1);
		}
	    }
	});

	JButton discountHistoryButton = new JButton("DISCOUNTS HISTORY");
	discountHistoryButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (isLoggedIn) {
		    panelMain.setVisible(false);
		    panelHistories.setVisible(true);
		    formClientRequest.getRootPane().setDefaultButton(backButtonDiscounts);
		} else {
		    panelMain.setVisible(false);
		    panelAuthentication.setVisible(true);
		    loginDispatcher = LOG_IN_HISTORIES;
		    usernameTextField.requestFocusInWindow();
		    formClientRequest.getRootPane().setDefaultButton(loginButton);
		}
	    }
	});

	setupMainPanelButtons(panelMain, productsDropDownList, ordersProfilesButton, exitButton, submitButton,
		discountHistoryButton);
	formClientRequest.getRootPane().setDefaultButton(submitButton);

	// ---------------------- PANEL PROFILES ---------------------//

	JLabel clientIdLabelOrders = new JLabel("CLIENT ID:");
	JLabel profileOfAllLabel = new JLabel("PROFILE OF ALL ORDERS");
	JLabel profileOfClientLabel = new JLabel("PROFILE OF SPECIFIC CLIENT:");
	JLabel ordersCaptionLabel = new JLabel("ORDER & CLIENT PROFILES");
	clientIdTextFieldOrders = new JTextField();
	setupProfilesPanelLabels(panelProfiles, ordersCaptionLabel, profileOfClientLabel, profileOfAllLabel,
		clientIdLabelOrders);

	JTextArea profileOfAllTextArea = new JTextArea();
	JTextArea profileClientTextArea = new JTextArea();
	setupProfilesPanelAreaa(panelProfiles, profileClientTextArea, profileOfAllTextArea);

	JButton ordersButton = new JButton("ORDERS PROFILE");
	ordersButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		String profile = "";
		try {
		    profile = assemleProfile();
		    showProfile(profileOfAllTextArea, profile);
		} catch (SQLException sqle) {
		    LOG.error("Problem accessing DB: ", sqle);
		    JOptionPane.showMessageDialog(null, "Problem accessing DB: " + sqle.getMessage(), "ERROR",
			    JOptionPane.ERROR_MESSAGE);
		}
	    }

	    private void showProfile(JTextArea profileOfAllTextArea, String profile) {
		profile = "ORDERS\tITEMS\tPROFITS\n" + profile;
		profileOfAllTextArea.setText(profile);
	    }

	    private String assemleProfile() throws SQLException {
		String profile = "";
		request = dbConnection.prepareStatement("call ordersProfile;");
		response = request.executeQuery();
		while (response.next()) {
		    profile = response.getString(1) + "\t" + response.getString(2) + "\t" + response.getString(3);
		}
		return profile;
	    }
	});

	JButton clientProfileButton = new JButton("CLIENT PROFILE");
	clientProfileButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		String profile = "";
		int clientId = 0;

		try {
		    clientId = Integer.parseInt(clientIdTextFieldOrders.getText());
		    profile = assembleClientProfile(clientId);
		    outputClientProfile(profileClientTextArea, profile);
		} catch (SQLException sqle) {
		    LOG.error("Problem accessing DB: ", sqle);
		    JOptionPane.showMessageDialog(null, "Problem accessing DB: " + sqle.getMessage(), "ERROR",
			    JOptionPane.ERROR_MESSAGE);
		}
	    }

	    private void outputClientProfile(JTextArea profileClientTextArea, String profile) {
		profile = "ORDERS\tITEMS\tPROFITS\n" + profile;
		profileClientTextArea.setText(profile);
	    }

	    private String assembleClientProfile(int clientID) throws SQLException {
		String clientProfile = "";
		request = dbConnection.prepareStatement("call clientProfile(" + clientID + ");");
		response = request.executeQuery();
		while (response.next()) {
		    clientProfile = response.getString(1) + "\t" + response.getString(2) + "\t" + response.getString(3);
		}
		return clientProfile;
	    }
	});

	backButtonOrders = new JButton("BACK TO MAIN");
	backButtonOrders.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		panelMain.setVisible(true);
		panelProfiles.setVisible(false);
		formClientRequest.getRootPane().setDefaultButton(submitButton);
	    }
	});

	JButton exitButtonOrders = new JButton("LOGOUT");
	exitButtonOrders.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		panelProfiles.setVisible(false);
		panelMain.setVisible(true);
		isLoggedIn = false;
		formClientRequest.getRootPane().setDefaultButton(submitButton);
	    }
	});

	setupProfilesPanelButtons(panelProfiles, exitButtonOrders, clientProfileButton, ordersButton);

	// ---------------------- PANEL HISTORIES ---------------------//

	JLabel dicountsCaptionLabel = new JLabel("DISCOUNTS HISTORY");
	JLabel clientIdLabelDiscounts = new JLabel("CLIENT NAME:");
	setupHistoriesPanelLabels(panelHistories, clientIdLabelDiscounts, dicountsCaptionLabel);

	clientIdTextFieldDiscounts = new JTextField();
	JTextArea discountsHistoryTextArea = new JTextArea();
	setupHistoriesPanelAreas(panelHistories, discountsHistoryTextArea);

	JButton allViewButton = new JButton("VIEW ALL");
	allViewButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		String report = "";
		try {
		    report = assembleDiscountReport();
		    outputDiscountReport(discountsHistoryTextArea, report);
		} catch (SQLException sqle) {
		    LOG.error("Problem accessing DB: ", sqle);
		    JOptionPane.showMessageDialog(null, "Problem accessing DB: " + sqle.getMessage(), "ERROR",
			    JOptionPane.ERROR_MESSAGE);
		}
	    }

	    private void outputDiscountReport(JTextArea discountsHistoryTextArea, String report) {
		String captions = "";
		captions = " client\t         discount\n";
		discountsHistoryTextArea.setText(captions + report);
	    }

	    private String assembleDiscountReport() throws SQLException {
		String report = "";
		String reportEntry = "";

		request = dbConnection.prepareStatement("select *from discountHistory;");
		response = request.executeQuery();
		while (response.next()) {
		    reportEntry = " " + response.getString(1) + "             " + response.getString(2);
		    report = report + "\n" + reportEntry;
		}
		return report;
	    }
	});

	JButton clientViewButton = new JButton("VIEW PER CLIENT");
	clientViewButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		String clientDiscountReport = "";
		try {
		    clientDiscountReport = assembleClientDiscountReport();
		    outputClientDiscountReport(discountsHistoryTextArea, clientDiscountReport);
		} catch (SQLException sqle) {
		    LOG.error("Problem accessing DB: ", sqle);
		    JOptionPane.showMessageDialog(null, "Problem accessing DB: " + sqle.getMessage(), "ERROR",
			    JOptionPane.ERROR_MESSAGE);
		}
	    }

	    private void outputClientDiscountReport(JTextArea discountsHistoryTextArea, String clientDiscountReport) {
		String captions;
		captions = " client\t         discount\n";
		discountsHistoryTextArea.setText(captions + clientDiscountReport);
	    }

	    private String assembleClientDiscountReport() throws SQLException {
		String report = "";
		String reportEntry = "";

		String clientName = clientIdTextFieldDiscounts.getText();
		String query = "select *from discountHistory where client=" + "\"" + clientName + "\""
			+ "order by discount;";

		request = dbConnection.prepareStatement(query);
		response = request.executeQuery();

		while (response.next()) {
		    reportEntry = " " + response.getString(1) + "             " + response.getString(2);
		    report = report + "\n" + reportEntry;
		}
		return report;
	    }
	});

	backButtonDiscounts = new JButton("BACK TO MAIN");
	backButtonDiscounts.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		panelMain.setVisible(true);
		panelHistories.setVisible(false);
		formClientRequest.getRootPane().setDefaultButton(submitButton);
	    }
	});

	JButton exitButtonDiscounts = new JButton("LOGOUT");
	exitButtonDiscounts.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		panelHistories.setVisible(false);
		panelMain.setVisible(true);
		isLoggedIn = false;
		formClientRequest.getRootPane().setDefaultButton(submitButton);
	    }
	});

	setupHistoriesPanelButtons(panelHistories, exitButtonDiscounts, allViewButton, clientViewButton);

	// ---------------------- PANEL AUTHENTICATION ---------------------//

	JLabel loginCaptionLabel = new JLabel("AUTHENTICATION FORM");
	JLabel usernameLabel = new JLabel("USERNAME:");
	JLabel passwordLabel = new JLabel("PASSWORD:");
	setupAuthenticationPanelLabels(panelAuthentication, passwordLabel, usernameLabel, loginCaptionLabel);

	passwordTextField = new JPasswordField();
	usernameTextField = new JTextField();
	setupAuthenticationPanelAreas(panelAuthentication);

	loginButton = new JButton("LOG IN");
	loginButton.addActionListener(new ActionListener() {
	    @SuppressWarnings("deprecation")
	    public void actionPerformed(ActionEvent e) {
		String username = usernameTextField.getText();
		String password = passwordTextField.getText();

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
			usernameTextField.setBackground(Color.RED);
			passwordLabel.setForeground(Color.RED);
			passwordTextField.setBackground(Color.RED);
		    } else if (!username.equalsIgnoreCase(DBConstants.USER)) {
			usernameLabel.setForeground(Color.RED);
			usernameTextField.setBackground(Color.RED);
		    } else if (!password.equals(DBConstants.PASSWORD)) {
			passwordLabel.setForeground(Color.RED);
			passwordTextField.setBackground(Color.RED);
		    }
		    JOptionPane.showMessageDialog(null, "Wrong username/password!!!", "ERROR",
			    JOptionPane.ERROR_MESSAGE);
		}
	    }

	    private void clearFields() {
		passwordTextField.setText("");
		usernameTextField.setText("");
		usernameTextField.setBackground(Color.WHITE);
		passwordTextField.setBackground(Color.WHITE);
		usernameLabel.setForeground(Color.YELLOW);
		passwordLabel.setForeground(Color.YELLOW);
	    }

	    private void proceedToPanel(JPanel currentPannel, JPanel nextPanel) {
		currentPannel.setVisible(false);
		nextPanel.setVisible(true);
		isLoggedIn = true;
		if (loginDispatcher == LOG_IN_PROFILES) {
		    formClientRequest.getRootPane().setDefaultButton(backButtonOrders);
		} else {
		    formClientRequest.getRootPane().setDefaultButton(backButtonDiscounts);
		}
	    }
	});

	JButton backButtonLogin = new JButton("BACK TO MAIN");
	backButtonLogin.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		panelMain.setVisible(true);
		panelAuthentication.setVisible(false);

		passwordTextField.setText("");
		usernameTextField.setText("");
		usernameTextField.setBackground(Color.WHITE);
		passwordTextField.setBackground(Color.WHITE);
		usernameLabel.setForeground(Color.YELLOW);
		passwordLabel.setForeground(Color.YELLOW);
	    }
	});

	setupAuthenticationPanelButtons(panelAuthentication, backButtonLogin, loginButton);
    }

    private void setupForm() {
	formClientRequest = new JFrame();
	formClientRequest.setResizable(false);
	formClientRequest.getContentPane().setBackground(Color.BLACK);
	formClientRequest.setTitle("CLIENT REQUEST");
	formClientRequest.setBounds(100, 100, 493, 411);
	formClientRequest.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	formClientRequest.getContentPane().setLayout(new CardLayout(0, 0));
	formClientRequest.setIconImage(new ImageIcon(this.getClass().getResource("/storage.png")).getImage());
    }

    private void initResources() throws UnknownHostException, IOException, ClassNotFoundException, SQLException {
	clientConnection = new Socket(host, WarehouseServer.port);
	dbConnection = DatabaseConnector.getInstance().getConnection();
	responseStream = new DataInputStream(clientConnection.getInputStream());
	requestStream = new DataOutputStream(clientConnection.getOutputStream());
    }

    private void initComboBox() throws Exception {
	String query = "select `group`,`brand`,`model` from products order by `group`;";
	request = dbConnection.prepareStatement(query);
	response = request.executeQuery();
	productsList = new ArrayList<String>();

	while (response.next()) {
	    productsList.add(response.getString(1) + " " + response.getString(2) + " " + response.getString(3));
	}
    }

    private void setupPanels(JPanel panelMain, JPanel panelAuthentication, JPanel panelProfiles, JPanel panelHistories) {
	formClientRequest.getContentPane().add(panelMain, "name_23060868800636");
	panelMain.setBackground(Color.BLACK);
	panelMain.setLayout(null);

	panelAuthentication.setBackground(Color.BLACK);
	formClientRequest.getContentPane().add(panelAuthentication, "name_39712632177488");
	panelAuthentication.setLayout(null);

	panelProfiles.setForeground(new Color(0, 0, 0));
	panelProfiles.setBackground(Color.BLACK);
	formClientRequest.getContentPane().add(panelProfiles, "name_23060916454223");
	panelProfiles.setLayout(null);

	panelHistories.setBackground(Color.BLACK);
	formClientRequest.getContentPane().add(panelHistories, "name_36511470292507");
	panelHistories.setLayout(null);
    }

    private void setupMainPanelLabels(JPanel panelMain, JLabel clientIdLabel, JLabel productIdLabel,
	    JLabel sectionLabel, JLabel quantityLabel) {
	clientIdLabel.setForeground(Color.YELLOW);
	clientIdLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));
	clientIdLabel.setBounds(71, 75, 59, 27);
	panelMain.add(clientIdLabel);

	productIdLabel.setForeground(Color.YELLOW);
	productIdLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));
	productIdLabel.setBounds(69, 8, 108, 27);
	panelMain.add(productIdLabel);

	sectionLabel.setForeground(new Color(255, 255, 0));
	sectionLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
	sectionLabel.setBounds(0, 273, 487, 14);
	panelMain.add(sectionLabel);

	orderCaptionLabel.setForeground(Color.YELLOW);
	orderCaptionLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
	orderCaptionLabel.setBounds(284, 21, 100, 14);
	panelMain.add(orderCaptionLabel);

	quantityLabel.setForeground(Color.YELLOW);
	quantityLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));
	quantityLabel.setBounds(71, 130, 59, 27);
	panelMain.add(quantityLabel);

    }

    private void setupMainPanelFieldsAndArea(JPanel panelMain) {
	quantityTextField.setColumns(10);
	quantityTextField.setBounds(61, 154, 86, 27);
	panelMain.add(quantityTextField);

	clientIdTextField.setBounds(61, 100, 86, 27);
	panelMain.add(clientIdTextField);
	clientIdTextField.setColumns(10);

	responseTextArea.setBackground(new Color(255, 255, 255));
	responseTextArea.setEditable(false);
	responseTextArea.setBounds(231, 43, 233, 147);
	panelMain.add(responseTextArea);
    }

    private void setupMainPanelButtons(JPanel panelMain, JComboBox<Object> productsDropDownList,
	    JButton ordersProfilesButton, JButton exitButton, JButton submitButton, JButton discountHistoryButton) {
	ordersProfilesButton.setFont(new Font("Tahoma", Font.BOLD, 12));
	ordersProfilesButton.setBounds(25, 298, 184, 61);
	panelMain.add(ordersProfilesButton);

	discountHistoryButton.setFont(new Font("Tahoma", Font.BOLD, 12));
	discountHistoryButton.setBounds(231, 299, 233, 61);
	panelMain.add(discountHistoryButton);

	exitButton.setBounds(231, 201, 232, 61);
	exitButton.setForeground(UIManager.getColor("Button.foreground"));
	exitButton.setFont(new Font("Tahoma", Font.BOLD, 14));
	exitButton.setBackground(UIManager.getColor("Button.background"));
	panelMain.add(exitButton);

	submitButton.setBounds(25, 201, 184, 61);
	submitButton.setForeground(UIManager.getColor("Button.foreground"));
	submitButton.setFont(new Font("Tahoma", Font.BOLD, 14));
	submitButton.setBackground(UIManager.getColor("Button.background"));
	panelMain.add(submitButton);

	productsDropDownList.setBounds(25, 37, 184, 27);
	productsDropDownList.setFont(new Font("Tahoma", Font.BOLD, 12));
	panelMain.add(productsDropDownList);
    }

    private void setupProfilesPanelLabels(JPanel panelProfiles, JLabel ordersCaptionLabel, JLabel profileOfClientLabel,
	    JLabel profileOfAllLabel, JLabel clientIdLabelOrders) {
	ordersCaptionLabel.setForeground(new Color(255, 255, 0));
	ordersCaptionLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
	ordersCaptionLabel.setBounds(144, 11, 208, 14);
	panelProfiles.add(ordersCaptionLabel);

	profileOfClientLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));
	profileOfClientLabel.setForeground(new Color(255, 255, 0));
	profileOfClientLabel.setBounds(208, 165, 166, 14);
	panelProfiles.add(profileOfClientLabel);

	profileOfAllLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));
	profileOfAllLabel.setForeground(new Color(255, 255, 0));
	profileOfAllLabel.setBounds(205, 52, 147, 14);
	panelProfiles.add(profileOfAllLabel);

	clientIdLabelOrders.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));
	clientIdLabelOrders.setForeground(new Color(255, 255, 0));
	clientIdLabelOrders.setBounds(32, 133, 73, 14);
	panelProfiles.add(clientIdLabelOrders);

	clientIdTextFieldOrders.setBounds(33, 151, 86, 20);
	clientIdTextFieldOrders.setColumns(10);
	panelProfiles.add(clientIdTextFieldOrders);

    }

    private void setupProfilesPanelAreaa(JPanel panelProfiles, JTextArea profileClientTextArea,
	    JTextArea profileOfAllTextArea) {
	profileClientTextArea.setEditable(false);
	profileClientTextArea.setBounds(205, 182, 252, 39);
	panelProfiles.add(profileClientTextArea);

	profileOfAllTextArea.setEditable(false);
	profileOfAllTextArea.setBounds(205, 70, 252, 39);
	panelProfiles.add(profileOfAllTextArea);
    }

    private void setupProfilesPanelButtons(JPanel panelProfiles, JButton exitButtonOrders, JButton clientProfileButton,
	    JButton ordersButton) {
	exitButtonOrders.setFont(new Font("Tahoma", Font.BOLD, 12));
	exitButtonOrders.setBounds(234, 282, 223, 64);
	panelProfiles.add(exitButtonOrders);

	backButtonOrders.setFont(new Font("Tahoma", Font.BOLD, 11));
	backButtonOrders.setBounds(33, 283, 187, 64);
	panelProfiles.add(backButtonOrders);

	clientProfileButton.setFont(new Font("Tahoma", Font.BOLD, 11));
	clientProfileButton.setBounds(33, 182, 147, 39);
	panelProfiles.add(clientProfileButton);

	ordersButton.setFont(new Font("Tahoma", Font.BOLD, 11));
	ordersButton.setBounds(33, 70, 147, 39);
	panelProfiles.add(ordersButton);
    }

    private void setupHistoriesPanelLabels(JPanel panelHistories, JLabel clientIdLabelDiscounts,
	    JLabel dicountsCaptionLabel) {
	clientIdLabelDiscounts.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));
	clientIdLabelDiscounts.setForeground(new Color(255, 255, 0));
	clientIdLabelDiscounts.setBounds(319, 121, 84, 14);
	panelHistories.add(clientIdLabelDiscounts);

	dicountsCaptionLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
	dicountsCaptionLabel.setForeground(new Color(255, 255, 0));
	dicountsCaptionLabel.setBounds(82, 14, 162, 14);
	panelHistories.add(dicountsCaptionLabel);
    }

    private void setupHistoriesPanelAreas(JPanel panelHistories, JTextArea discountsHistoryTextArea) {
	clientIdTextFieldDiscounts.setBounds(319, 143, 141, 30);
	clientIdTextFieldDiscounts.setColumns(10);
	panelHistories.add(clientIdTextFieldDiscounts);

	discountsHistoryTextArea.setEditable(false);
	discountsHistoryTextArea.setBounds(33, 39, 266, 200);
	panelHistories.add(discountsHistoryTextArea);
    }

    private void setupHistoriesPanelButtons(JPanel panelHistories, JButton exitButtonDiscounts, JButton allViewButton,
	    JButton clientViewButton) {
	exitButtonDiscounts.setFont(new Font("Tahoma", Font.BOLD, 12));
	exitButtonDiscounts.setBounds(237, 281, 223, 64);
	panelHistories.add(exitButtonDiscounts);

	backButtonDiscounts.setFont(new Font("Tahoma", Font.BOLD, 11));
	backButtonDiscounts.setBounds(33, 283, 187, 63);
	panelHistories.add(backButtonDiscounts);

	allViewButton.setFont(new Font("Tahoma", Font.BOLD, 11));
	allViewButton.setBounds(319, 39, 141, 55);
	panelHistories.add(allViewButton);

	clientViewButton.setFont(new Font("Tahoma", Font.BOLD, 11));
	clientViewButton.setBounds(319, 184, 141, 55);
	panelHistories.add(clientViewButton);
    }

    private void setupAuthenticationPanelLabels(JPanel panelAuthentication, JLabel passwordLabel, JLabel usernameLabel,
	    JLabel loginCaptionLabel) {
	passwordLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));
	passwordLabel.setForeground(new Color(255, 255, 0));
	passwordLabel.setBounds(122, 142, 96, 14);
	panelAuthentication.add(passwordLabel);

	usernameLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));
	usernameLabel.setForeground(new Color(255, 255, 0));
	usernameLabel.setBounds(122, 77, 113, 14);
	panelAuthentication.add(usernameLabel);

	loginCaptionLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
	loginCaptionLabel.setForeground(new Color(255, 255, 0));
	loginCaptionLabel.setBounds(147, 23, 199, 14);
	panelAuthentication.add(loginCaptionLabel);
    }

    private void setupAuthenticationPanelAreas(JPanel panelAuthentication) {
	usernameTextField.setBounds(122, 102, 220, 29);
	panelAuthentication.add(usernameTextField);
	usernameTextField.setColumns(10);

	passwordTextField.setBounds(122, 167, 220, 41);
	panelAuthentication.add(passwordTextField);

    }

    private void setupAuthenticationPanelButtons(JPanel panelAuthentication, JButton backButtonLogin,
	    JButton loginButton) {
	backButtonLogin.setFont(new Font("Tahoma", Font.BOLD, 12));
	backButtonLogin.setBounds(261, 242, 176, 66);
	panelAuthentication.add(backButtonLogin);

	loginButton.setFont(new Font("Tahoma", Font.BOLD, 12));
	loginButton.setBounds(71, 242, 164, 66);
	panelAuthentication.add(loginButton);
    }
}