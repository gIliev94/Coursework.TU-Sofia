package warehouse;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.JComboBox;

import java.awt.CardLayout;

import javax.swing.JPasswordField;

public class Warehouse_client {

    // /////////////////////////////
    private JFrame formClientRequest;
    private JTextArea responseTextArea;
    private JLabel orderCaptionLabel;
    private JTextField clientIdTextField;
    private JTextField quantityTextField;
    private JTextField clientIdTextFieldOrders;
    private JTextField clientIdTextFieldDiscounts;
    private JPasswordField passwordTextField;
    private JTextField usernameTextField;

    private DataInputStream din;
    private DataOutputStream dout;

    private Connection dbConnection;
    private PreparedStatement stat;
    private ResultSet rs;

    private static final String host = "localhost";
    private Socket clientConnection;

    public static List<String> productsList;
    private int product = 0;
    private int logIndicator = 0;

    // ////////////////////////////
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		try {
		    Warehouse_client window = new Warehouse_client();
		    window.formClientRequest.setVisible(true);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    /**
     * Create the application.
     * 
     * @throws Exception
     */
    public Warehouse_client() throws Exception {
	initResources();
	initComboBox();
	initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() throws Exception {
	setupForm();

	JPanel panelMain = new JPanel();
	formClientRequest.getContentPane()
		.add(panelMain, "name_23060868800636");
	panelMain.setBackground(new Color(0, 128, 128));
	panelMain.setLayout(null);

	JLabel clientIdLabel = new JLabel("Client ID:");
	clientIdLabel.setForeground(Color.YELLOW);
	clientIdLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));
	clientIdLabel.setBounds(71, 75, 59, 27);
	panelMain.add(clientIdLabel);

	JLabel productIdLabel = new JLabel("Product:");
	productIdLabel.setForeground(Color.YELLOW);
	productIdLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));
	productIdLabel.setBounds(69, 8, 108, 27);
	panelMain.add(productIdLabel);

	JLabel quantityLabel = new JLabel("Quantity:");
	quantityLabel.setForeground(Color.YELLOW);
	quantityLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));
	quantityLabel.setBounds(71, 130, 59, 27);
	panelMain.add(quantityLabel);

	responseTextArea = new JTextArea();
	responseTextArea.setBackground(new Color(255, 255, 255));
	responseTextArea.setEditable(false);
	responseTextArea.setBounds(231, 43, 233, 147);
	panelMain.add(responseTextArea);

	orderCaptionLabel = new JLabel("YOUR ORDER:");
	orderCaptionLabel.setForeground(Color.YELLOW);
	orderCaptionLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
	orderCaptionLabel.setBounds(284, 21, 100, 14);
	panelMain.add(orderCaptionLabel);

	JButton submitButton = new JButton("SUBMIT");
	submitButton.setForeground(UIManager.getColor("Button.foreground"));
	submitButton.setFont(new Font("Tahoma", Font.BOLD, 14));
	submitButton.setBackground(UIManager.getColor("Button.background"));
	submitButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		try {
		    // Getting the data from client input
		    int client = Integer.parseInt(clientIdTextField.getText());
		    int prod = product;
		    int quantity = Integer.parseInt(quantityTextField.getText());

		    // Sending the data to the server(more like to its thread)
		    sendOrderRequest(client, prod, quantity);
		    // Getting the response from the server and displaying it
		    receiveResponse(submitButton);
		} catch (Exception e) {
		    JOptionPane
			    .showMessageDialog(
				    null,
				    "Invalid data input!\nPlease enter correct values...",
				    "ERROR", JOptionPane.ERROR_MESSAGE);
		    System.out.println(e.getMessage());
		}
	    }

	    private void receiveResponse(JButton submitButton)
		    throws IOException {
		String msg;
		msg = din.readUTF();
		responseTextArea.setText(msg);
		submitButton.setEnabled(false);
	    }

	    private void sendOrderRequest(int client, int prod, int quantity)
		    throws IOException {
		dout.writeInt(client);
		dout.writeInt(prod);
		dout.writeInt(quantity);
	    }
	});
	submitButton.setBounds(25, 201, 184, 61);
	panelMain.add(submitButton);

	clientIdTextField = new JTextField();
	clientIdTextField.setBounds(61, 100, 86, 20);
	panelMain.add(clientIdTextField);
	clientIdTextField.setColumns(10);

	quantityTextField = new JTextField();
	quantityTextField.setColumns(10);
	quantityTextField.setBounds(61, 154, 86, 20);
	panelMain.add(quantityTextField);

	JButton exitButton = new JButton("EXIT");
	exitButton.setForeground(UIManager.getColor("Button.foreground"));
	exitButton.setFont(new Font("Tahoma", Font.BOLD, 14));
	exitButton.setBackground(UIManager.getColor("Button.background"));
	exitButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		formClientRequest.dispose();
	    }
	});
	exitButton.setBounds(231, 201, 232, 61);
	panelMain.add(exitButton);

	JComboBox<Object> productsDropDownList = new JComboBox<>(
		productsList.toArray());
	productsDropDownList.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		try {
		    String str = (String) productsDropDownList
			    .getSelectedItem();
		    String productSubstr = str.substring(str.length() - 3);

		    findProduct(productSubstr);
		} catch (Exception p) {
		    JOptionPane.showMessageDialog(null, p.getMessage(),
			    "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	    }

	    private void findProduct(String productSubstr) throws SQLException {
		stat = dbConnection
			.prepareStatement("select `id` from products where `model` like "
				+ "\"%" + productSubstr + "\"" + ";");
		rs = stat.executeQuery();
		while (rs.next()) {
		    product = rs.getInt(1);
		}
	    }
	});
	productsDropDownList.setBounds(25, 37, 184, 27);
	productsDropDownList.setFont(new Font("Tahoma", Font.BOLD, 12));
	productsDropDownList.setSelectedItem(null);
	panelMain.add(productsDropDownList);

	JPanel panelAuthentication = new JPanel();
	panelAuthentication.setBackground(new Color(0, 128, 128));
	formClientRequest.getContentPane().add(panelAuthentication,
		"name_39712632177488");
	panelAuthentication.setLayout(null);

	JPanel panelProfiles = new JPanel();
	panelProfiles.setForeground(new Color(0, 0, 0));
	panelProfiles.setBackground(new Color(0, 128, 128));
	formClientRequest.getContentPane().add(panelProfiles,
		"name_23060916454223");
	panelProfiles.setLayout(null);

	JButton ordersProfilesButton = new JButton("ORDERS & PROFITS");
	ordersProfilesButton.setFont(new Font("Tahoma", Font.BOLD, 12));
	ordersProfilesButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		// Goes to authentication form and sets a variable to indicate
		// we`re trying to log in this form("ORDERS & PROFITS")
		panelMain.setVisible(false);
		panelAuthentication.setVisible(true);
		logIndicator = 1;
	    }
	});
	ordersProfilesButton.setBounds(25, 298, 184, 61);
	panelMain.add(ordersProfilesButton);

	JLabel sectionLabel = new JLabel(
		"----------------------------EMPLOYEE SECTION----------------------------");
	sectionLabel.setForeground(new Color(255, 255, 0));
	sectionLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
	sectionLabel.setBounds(0, 273, 487, 14);
	panelMain.add(sectionLabel);

	JTextArea profileOfAllTextArea = new JTextArea();
	profileOfAllTextArea.setEditable(false);
	profileOfAllTextArea.setBounds(205, 70, 252, 39);
	panelProfiles.add(profileOfAllTextArea);

	JTextArea profileClientTextArea = new JTextArea();
	profileClientTextArea.setEditable(false);
	profileClientTextArea.setBounds(205, 182, 252, 39);
	panelProfiles.add(profileClientTextArea);

	JButton ordersButton = new JButton("ORDERS PROFILE");
	ordersButton.setFont(new Font("Tahoma", Font.BOLD, 11));
	ordersButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		// /////////////////////////////
		String representation = "";
		try {
		    representation = buildProfile(representation);
		    outputProfile(profileOfAllTextArea, representation);
		} catch (SQLException e) {
		    JOptionPane.showMessageDialog(null, e.getMessage(),
			    "ERROR", JOptionPane.ERROR_MESSAGE);
		}
		// ////////////////////////////
	    }

	    private void outputProfile(JTextArea profileOfAllTextArea,
		    String representation) {
		representation = "ORDERS\tITEMS\tPROFITS\n" + representation;
		profileOfAllTextArea.setText(representation);
	    }

	    private String buildProfile(String st) throws SQLException {
		stat = dbConnection.prepareStatement("call ordersProfile;");
		rs = stat.executeQuery();
		while (rs.next()) {
		    st = rs.getString(1) + "\t" + rs.getString(2) + "\t"
			    + rs.getString(3);
		}
		return st;
	    }
	});
	ordersButton.setBounds(33, 70, 147, 39);
	panelProfiles.add(ordersButton);

	clientIdTextFieldOrders = new JTextField();
	clientIdTextFieldOrders.setBounds(33, 151, 86, 20);
	panelProfiles.add(clientIdTextFieldOrders);
	clientIdTextFieldOrders.setColumns(10);

	JLabel clientIdLabelOrders = new JLabel("CLIENT ID:");
	clientIdLabelOrders.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC,
		11));
	clientIdLabelOrders.setForeground(new Color(255, 255, 0));
	clientIdLabelOrders.setBounds(32, 133, 73, 14);
	panelProfiles.add(clientIdLabelOrders);

	JButton clientProfileButton = new JButton("CLIENT PROFILE");
	clientProfileButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		// /////////////////////////////
		String representation = "";
		int clientID = 0;
		try {
		    clientID = Integer.parseInt(clientIdTextFieldOrders
			    .getText());
		    representation = buildClientProfile(representation,
			    clientID);
		    outputClientProfile(profileClientTextArea, representation);
		} catch (SQLException e1) {
		    JOptionPane.showMessageDialog(null, e1.getMessage(),
			    "ERROR", JOptionPane.ERROR_MESSAGE);
		}
		// /////////////////////////////
	    }

	    private void outputClientProfile(JTextArea profileClientTextArea,
		    String representation) {
		representation = "ORDERS\tITEMS\tPROFITS\n" + representation;
		profileClientTextArea.setText(representation);
	    }

	    private String buildClientProfile(String str, int clientID)
		    throws SQLException {
		stat = dbConnection.prepareStatement("call clientProfile("
			+ clientID + ");");
		rs = stat.executeQuery();
		while (rs.next()) {
		    str = rs.getString(1) + "\t" + rs.getString(2) + "\t"
			    + rs.getString(3);
		}
		return str;
	    }
	});
	clientProfileButton.setFont(new Font("Tahoma", Font.BOLD, 11));
	clientProfileButton.setBounds(33, 182, 147, 39);
	panelProfiles.add(clientProfileButton);

	JButton backButtonOrders = new JButton("BACK TO MAIN");
	backButtonOrders.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		panelMain.setVisible(true);
		panelProfiles.setVisible(false);
	    }
	});
	backButtonOrders.setFont(new Font("Tahoma", Font.BOLD, 11));
	backButtonOrders.setBounds(33, 283, 187, 64);
	panelProfiles.add(backButtonOrders);

	JLabel profileOfAllLabel = new JLabel("PROFILE OF ALL ORDERS");
	profileOfAllLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC,
		11));
	profileOfAllLabel.setForeground(new Color(255, 255, 0));
	profileOfAllLabel.setBounds(205, 52, 147, 14);
	panelProfiles.add(profileOfAllLabel);

	JButton exitButtonOrders = new JButton("EXIT");
	exitButtonOrders.setFont(new Font("Tahoma", Font.BOLD, 12));
	exitButtonOrders.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		formClientRequest.dispose();
	    }
	});
	exitButtonOrders.setBounds(234, 282, 223, 64);
	panelProfiles.add(exitButtonOrders);

	JLabel profileOfClientLabel = new JLabel("PROFILE OF SPECIFIC CLIENT:");
	profileOfClientLabel.setFont(new Font("Tahoma",
		Font.BOLD | Font.ITALIC, 11));
	profileOfClientLabel.setForeground(new Color(255, 255, 0));
	profileOfClientLabel.setBounds(208, 165, 166, 14);
	panelProfiles.add(profileOfClientLabel);

	JLabel ordersCaptionLabel = new JLabel("ORDER & CLIENT PROFILES");
	ordersCaptionLabel.setForeground(new Color(255, 255, 0));
	ordersCaptionLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
	ordersCaptionLabel.setBounds(144, 11, 208, 14);
	panelProfiles.add(ordersCaptionLabel);

	JPanel panelHistories = new JPanel();
	panelHistories.setBackground(new Color(0, 128, 128));
	formClientRequest.getContentPane().add(panelHistories,
		"name_36511470292507");
	panelHistories.setLayout(null);

	JButton discountHistoryButton = new JButton("DISCOUNTS HISTORY");
	discountHistoryButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		// Goes to authentication form and sets a variable to indicate
		// we`re trying to log in this form("DISCOUNTS HISTORY")
		panelMain.setVisible(false);
		panelAuthentication.setVisible(true);
		logIndicator = 2;
	    }
	});
	discountHistoryButton.setFont(new Font("Tahoma", Font.BOLD, 12));
	discountHistoryButton.setBounds(231, 299, 233, 61);
	panelMain.add(discountHistoryButton);

	JTextArea discountsHistoryTextArea = new JTextArea();
	discountsHistoryTextArea.setEditable(false);
	discountsHistoryTextArea.setBounds(33, 39, 266, 200);
	panelHistories.add(discountsHistoryTextArea);

	clientIdTextFieldDiscounts = new JTextField();
	clientIdTextFieldDiscounts.setBounds(319, 143, 141, 30);
	panelHistories.add(clientIdTextFieldDiscounts);
	clientIdTextFieldDiscounts.setColumns(10);

	JButton clientViewButton = new JButton("FOR CLIENT VIEW");
	clientViewButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		// /////////////////////////////
		String representation = "";
		try {
		    representation = buildClientDiscountReport(representation);
		    outputClientDiscountReport(discountsHistoryTextArea,
			    representation);
		} catch (SQLException m) {
		    JOptionPane.showMessageDialog(null, m.getMessage(),
			    "ERROR", JOptionPane.ERROR_MESSAGE);
		}
		// /////////////////////////////
	    }

	    private void outputClientDiscountReport(
		    JTextArea discountsHistoryTextArea, String representation) {
		String captions;
		captions = " client\t         discount\n";
		discountsHistoryTextArea.setText(captions + representation);
	    }

	    private String buildClientDiscountReport(String concat)
		    throws SQLException {
		String repString = "";
		String clientName = "";
		clientName = clientIdTextFieldDiscounts.getText();
		stat = dbConnection
			.prepareStatement("select *from discountHistory where client="
				+ "\""
				+ clientName
				+ "\""
				+ "order by discount;");
		rs = stat.executeQuery();
		while (rs.next()) {
		    repString = " " + rs.getString(1) + "             "
			    + rs.getString(2);
		    concat = concat + "\n" + repString;
		}
		return concat;
	    }
	});
	clientViewButton.setFont(new Font("Tahoma", Font.BOLD, 11));
	clientViewButton.setBounds(319, 184, 141, 55);
	panelHistories.add(clientViewButton);

	JButton allViewButton = new JButton("ALL VIEW");
	allViewButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		// /////////////////////////////
		String representation = "";
		try {
		    representation = buildDiscountReport(representation);
		    outputDiscountReport(discountsHistoryTextArea,
			    representation);
		} catch (SQLException m) {
		    JOptionPane.showMessageDialog(null, m.getMessage(),
			    "ERROR", JOptionPane.ERROR_MESSAGE);
		}
		// /////////////////////////////
	    }

	    private void outputDiscountReport(
		    JTextArea discountsHistoryTextArea, String concat) {
		String captions = "";
		captions = " client\t         discount\n";
		discountsHistoryTextArea.setText(captions + concat);
	    }

	    private String buildDiscountReport(String concat)
		    throws SQLException {
		String st = "";
		stat = dbConnection
			.prepareStatement("select *from discountHistory;");
		rs = stat.executeQuery();
		while (rs.next()) {
		    st = " " + rs.getString(1) + "             "
			    + rs.getString(2);
		    concat = concat + "\n" + st;
		}
		return concat;
	    }
	});
	allViewButton.setFont(new Font("Tahoma", Font.BOLD, 11));
	allViewButton.setBounds(319, 39, 141, 55);
	panelHistories.add(allViewButton);

	JLabel dicountsCaptionLabel = new JLabel("DISCOUNTS HISTORY");
	dicountsCaptionLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
	dicountsCaptionLabel.setForeground(new Color(255, 255, 0));
	dicountsCaptionLabel.setBounds(82, 14, 162, 14);
	panelHistories.add(dicountsCaptionLabel);

	JButton backButtonDiscounts = new JButton("BACK TO MAIN");
	backButtonDiscounts.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		// /////////////////////////////
		panelMain.setVisible(true);
		panelHistories.setVisible(false);
		// /////////////////////////////
	    }
	});
	backButtonDiscounts.setFont(new Font("Tahoma", Font.BOLD, 11));
	backButtonDiscounts.setBounds(33, 283, 187, 63);
	panelHistories.add(backButtonDiscounts);

	JLabel clientIdLabelDiscounts = new JLabel("CLIENT NAME:");
	clientIdLabelDiscounts.setFont(new Font("Tahoma", Font.BOLD
		| Font.ITALIC, 11));
	clientIdLabelDiscounts.setForeground(new Color(255, 255, 0));
	clientIdLabelDiscounts.setBounds(319, 121, 84, 14);
	panelHistories.add(clientIdLabelDiscounts);

	JButton exitButtonDiscounts = new JButton("EXIT");
	exitButtonDiscounts.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		formClientRequest.dispose();
	    }
	});
	exitButtonDiscounts.setFont(new Font("Tahoma", Font.BOLD, 12));
	exitButtonDiscounts.setBounds(237, 281, 223, 64);
	panelHistories.add(exitButtonDiscounts);

	passwordTextField = new JPasswordField();
	passwordTextField.setBounds(122, 167, 220, 41);
	panelAuthentication.add(passwordTextField);

	usernameTextField = new JTextField();
	usernameTextField.setBounds(122, 102, 220, 29);
	panelAuthentication.add(usernameTextField);
	usernameTextField.setColumns(10);

	// Log in form for some of the secondary panels
	JButton loginButton = new JButton("LOG IN");
	loginButton.addActionListener(new ActionListener() {
	    @SuppressWarnings("deprecation")
	    public void actionPerformed(ActionEvent e) {
		String username = usernameTextField.getText();
		String password = passwordTextField.getText();

		// Basically it refers the user to the form indicated by
		// variable(logIndicator) if correct user/pass are present
		if (username.equalsIgnoreCase(DataLayer.user)
			&& password.equals(DataLayer.password)
			&& logIndicator == 1) {
		    proceedProfiles(panelAuthentication, panelProfiles);
		    clearFields();
		} else if (username.equalsIgnoreCase(DataLayer.user)
			&& password.equals(DataLayer.password)
			&& logIndicator == 2) {
		    proceedProfiles(panelAuthentication, panelHistories);
		    clearFields();
		} else
		    JOptionPane.showMessageDialog(null,
			    "Wrong username/password!!!", "ERROR",
			    JOptionPane.ERROR_MESSAGE);
	    }

	    private void clearFields() {
		passwordTextField.setText("");
		usernameTextField.setText("");
	    }

	    private void proceedProfiles(JPanel panelAuthentication,
		    JPanel panelProfiles) {
		panelAuthentication.setVisible(false);
		panelProfiles.setVisible(true);
	    }
	});
	loginButton.setFont(new Font("Tahoma", Font.BOLD, 12));
	loginButton.setBounds(71, 242, 164, 66);
	panelAuthentication.add(loginButton);

	JButton backButtonLogin = new JButton("BACK TO MAIN");
	backButtonLogin.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		panelMain.setVisible(true);
		panelAuthentication.setVisible(false);
	    }
	});
	backButtonLogin.setFont(new Font("Tahoma", Font.BOLD, 12));
	backButtonLogin.setBounds(261, 242, 176, 66);
	panelAuthentication.add(backButtonLogin);

	JLabel loginCaptionLabel = new JLabel("AUTHENTICATION FORM");
	loginCaptionLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
	loginCaptionLabel.setForeground(new Color(255, 255, 0));
	loginCaptionLabel.setBounds(147, 23, 199, 14);
	panelAuthentication.add(loginCaptionLabel);

	JLabel usernameLabel = new JLabel("USERNAME:");
	usernameLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));
	usernameLabel.setForeground(new Color(255, 255, 0));
	usernameLabel.setBounds(122, 77, 113, 14);
	panelAuthentication.add(usernameLabel);

	JLabel passwordLabel = new JLabel("PASSWORD:");
	passwordLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));
	passwordLabel.setForeground(new Color(255, 255, 0));
	passwordLabel.setBounds(122, 142, 96, 14);
	panelAuthentication.add(passwordLabel);

    }

    private void setupForm() {
	formClientRequest = new JFrame();
	formClientRequest.setResizable(false);
	formClientRequest.getContentPane()
		.setBackground(new Color(0, 128, 128));
	formClientRequest.setTitle("CLIENT REQUEST");
	formClientRequest.setBounds(100, 100, 493, 411);
	formClientRequest.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	formClientRequest.getContentPane().setLayout(new CardLayout(0, 0));
    }

    private void initResources() throws UnknownHostException, IOException,
	    ClassNotFoundException, SQLException {
	clientConnection = new Socket(host, Warehouse_Server.port);
	dbConnection = new DataLayer().getConnection();
	din = new DataInputStream(clientConnection.getInputStream());
	dout = new DataOutputStream(clientConnection.getOutputStream());
    }

    // Initializes/Fills the ComboBox with the products
    private void initComboBox() throws Exception {
	stat = dbConnection
		.prepareStatement("select `group`,`brand`,`model` from products order by `group`;");
	rs = stat.executeQuery();
	productsList = new ArrayList<String>();

	while (rs.next()) {
	    productsList.add(rs.getString(1) + " " + rs.getString(2) + " "
		    + rs.getString(3));
	}
    }
}