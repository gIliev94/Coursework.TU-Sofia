package warehouse;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
	
	///////////////////////////////
	private JFrame frmClientRequest;
	private JTextArea textArea;
	private JLabel lblNewLabel_1;
	private JTextField textField;
	private JTextField textField_2;
	private DataInputStream din;
	private DataOutputStream dout;
	
	private Socket connection;
	static String host="localhost";      
	private Connection con=DriverManager.getConnection( Warehouse_Server.url, Warehouse_Server.user,Warehouse_Server.password);;
	private PreparedStatement stat;
	private ResultSet rs;
	public static ArrayList<String> prod;	
	private int product=0;					
	private int logIndicator=0;				
	
	private JTextField textField_1;
	private JTextField textField_3;
	private JPasswordField passwordField;
	private JTextField textField_4;
	//////////////////////////////
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Warehouse_client window = new Warehouse_client();
					window.frmClientRequest.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Warehouse_client()throws Exception {
		cbInit();       
		initialize();	
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings("rawtypes")
	private void initialize() throws Exception{
		frmClientRequest = new JFrame();
		frmClientRequest.setResizable(false);
		frmClientRequest.getContentPane().setBackground(new Color(0, 128, 128));
		frmClientRequest.setTitle("CLIENT REQUEST");
		frmClientRequest.setBounds(100, 100, 493, 411);
		frmClientRequest.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		////////////////////////////////////////////////////////
		connection=new Socket(host,Warehouse_Server.port);              
		din=new DataInputStream(connection.getInputStream());
		dout=new DataOutputStream(connection.getOutputStream());
		////////////////////////////////////////////////////////
		frmClientRequest.getContentPane().setLayout(new CardLayout(0, 0));
		JPanel panelMain = new JPanel();
		frmClientRequest.getContentPane().add(panelMain, "name_23060868800636");
		panelMain.setBackground(new Color(0, 128, 128));
		panelMain.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Client ID:");
		lblNewLabel.setForeground(Color.YELLOW);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));
		lblNewLabel.setBounds(71, 75, 59, 27);
		panelMain.add(lblNewLabel);
		
		JLabel lblProductId = new JLabel("Product:");
		lblProductId.setForeground(Color.YELLOW);
		lblProductId.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));
		lblProductId.setBounds(69, 8, 108, 27);
		panelMain.add(lblProductId);
		
		JLabel lblQuantity = new JLabel("Quantity:");
		lblQuantity.setForeground(Color.YELLOW);
		lblQuantity.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));
		lblQuantity.setBounds(71, 130, 59, 27);
		panelMain.add(lblQuantity);
		
		textArea = new JTextArea();
		textArea.setBackground(new Color(255, 255, 255));
		textArea.setEditable(false);
		textArea.setBounds(231, 43, 233, 147);
		panelMain.add(textArea);
		
		lblNewLabel_1 = new JLabel("YOUR ORDER:");
		lblNewLabel_1.setForeground(Color.YELLOW);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel_1.setBounds(284, 21, 100, 14);
		panelMain.add(lblNewLabel_1);
		
		JButton btnNewButton = new JButton("SUBMIT");
		btnNewButton.setForeground(UIManager.getColor("Button.foreground"));
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnNewButton.setBackground(UIManager.getColor("Button.background"));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
		try {
			//Getting the data from client input
			int client=Integer.parseInt(textField.getText());    
			int prod=product;
			int quantity=Integer.parseInt(textField_2.getText());
			String msg;
			//Sending the data to the server(more like to its thread)
				dout.writeInt(client);
				dout.writeInt(prod);
				dout.writeInt(quantity);
			//Getting the response from the server and displaying it in textArea
				msg = din.readUTF();
				textArea.setText(msg);
				btnNewButton.setEnabled(false);    
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,"Invalid data input!\nPlease enter correct values...", "ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnNewButton.setBounds(25, 201, 184, 61);
		panelMain.add(btnNewButton);
		
		textField = new JTextField();
		textField.setBounds(61, 100, 86, 20);
		panelMain.add(textField);
		textField.setColumns(10);
		
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(61, 154, 86, 20);
		panelMain.add(textField_2);
		
		JButton btnNewButton_1 = new JButton("EXIT");
		btnNewButton_1.setForeground(UIManager.getColor("Button.foreground"));
		btnNewButton_1.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnNewButton_1.setBackground(UIManager.getColor("Button.background"));
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frmClientRequest.dispose();    
			}
		});
		btnNewButton_1.setBounds(231, 201, 232, 61);
		panelMain.add(btnNewButton_1);
		
		@SuppressWarnings("unchecked")
		//Lets the client choose items from a drop down list
		JComboBox comboBox = new JComboBox(prod.toArray());
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		    	try {
					String str=(String) comboBox.getSelectedItem();   
					String substr=str.substring(str.length()-3);
					stat = con.prepareStatement("select `id` from products where `model` like "+"\"%"+substr+"\""+";");
			        rs = stat.executeQuery();
			        while(rs.next()){    
			        product=rs.getInt(1);
			        }
				} catch (Exception p) {
					JOptionPane.showMessageDialog(null,p.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		comboBox.setBounds(25, 37, 184, 27);
		comboBox.setFont(new Font("Tahoma", Font.BOLD, 12));
		comboBox.setSelectedItem(null);
		panelMain.add(comboBox);
		
		JPanel panelAuthentication = new JPanel();
		panelAuthentication.setBackground(new Color(0, 128, 128));
		frmClientRequest.getContentPane().add(panelAuthentication, "name_39712632177488");
		panelAuthentication.setLayout(null);
		
		JPanel panelProfiles = new JPanel();
		panelProfiles.setForeground(new Color(0, 0, 0));
		panelProfiles.setBackground(new Color(0, 128, 128));
		frmClientRequest.getContentPane().add(panelProfiles, "name_23060916454223");
		panelProfiles.setLayout(null);
		
		JButton btnNewButton_5 = new JButton("ORDERS & PROFITS");
		btnNewButton_5.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnNewButton_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Goes to authentication form and sets a variable to indicate we`re trying to log in this form("ORDERS & PROFITS")
				panelMain.setVisible(false);
				panelAuthentication.setVisible(true);
				logIndicator=1;
			}
		});
		btnNewButton_5.setBounds(25, 298, 184, 61);
		panelMain.add(btnNewButton_5);
		

		
		JLabel lblNewLabel_3 = new JLabel("----------------------------EMPLOYEE SECTION----------------------------");
		lblNewLabel_3.setForeground(new Color(255, 255, 0));
		lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel_3.setBounds(0, 273, 487, 14);
		panelMain.add(lblNewLabel_3);
		
		
		JTextArea textArea_1 = new JTextArea();
		textArea_1.setEditable(false);
		textArea_1.setBounds(205, 70, 252, 39);
		panelProfiles.add(textArea_1);
		
		JTextArea textArea_2 = new JTextArea();
		textArea_2.setEditable(false);
		textArea_2.setBounds(205, 182, 252, 39);
		panelProfiles.add(textArea_2);
		
		JButton btnNewButton_2 = new JButton("ORDERS PROFILE");
		btnNewButton_2.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0){
				///////////////////////////////
				String st="";
				try {
					stat=con.prepareStatement("call ordersProfile;");
					rs = stat.executeQuery();
					while(rs.next()){
				        st=rs.getString(1)+"\t"+rs.getString(2)+"\t"+rs.getString(3);
				        }
					st="ORDERS\tITEMS\tPROFITS\n"+st;
					textArea_1.setText(st);
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(null,e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
				}
				///////////////////////////////
			}
		});
		btnNewButton_2.setBounds(33, 70, 147, 39);
		panelProfiles.add(btnNewButton_2);
		
		textField_1 = new JTextField();
		textField_1.setBounds(33, 151, 86, 20);
		panelProfiles.add(textField_1);
		textField_1.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("CLIENT ID:");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));
		lblNewLabel_2.setForeground(new Color(255, 255, 0));
		lblNewLabel_2.setBounds(32, 133, 73, 14);
		panelProfiles.add(lblNewLabel_2);
		
		JButton btnNewButton_3 = new JButton("CLIENT PROFILE");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				///////////////////////////////
				String str="";
				int clientID=0;
				try {
				clientID=Integer.parseInt(textField_1.getText());
				stat=con.prepareStatement("call clientProfile("+clientID+");");
				rs = stat.executeQuery();
				while(rs.next()){
			        str=rs.getString(1)+"\t"+rs.getString(2)+"\t"+rs.getString(3);
			        }
				str="ORDERS\tITEMS\tPROFITS\n"+str;
				textArea_2.setText(str);
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null,e1.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
				}
				///////////////////////////////
			}
		});
		btnNewButton_3.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnNewButton_3.setBounds(33, 182, 147, 39);
		panelProfiles.add(btnNewButton_3);
		
		JButton btnNewButton_4 = new JButton("BACK TO MAIN");
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panelMain.setVisible(true);
				panelProfiles.setVisible(false);
			}
		});
		btnNewButton_4.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnNewButton_4.setBounds(33, 283, 187, 64);
		panelProfiles.add(btnNewButton_4);
		
		JLabel lblProfileOfAll = new JLabel("PROFILE OF ALL ORDERS");
		lblProfileOfAll.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));
		lblProfileOfAll.setForeground(new Color(255, 255, 0));
		lblProfileOfAll.setBounds(205, 52, 147, 14);
		panelProfiles.add(lblProfileOfAll);
		
		JButton btnNewButton_10 = new JButton("EXIT");
		btnNewButton_10.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnNewButton_10.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmClientRequest.dispose();
			}
		});
		btnNewButton_10.setBounds(234, 282, 223, 64);
		panelProfiles.add(btnNewButton_10);
		
		JLabel lblNewLabel_5 = new JLabel("PROFILE OF SPECIFIC CLIENT:");
		lblNewLabel_5.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));
		lblNewLabel_5.setForeground(new Color(255, 255, 0));
		lblNewLabel_5.setBounds(208, 165, 166, 14);
		panelProfiles.add(lblNewLabel_5);
		
		JLabel lblNewLabel_9 = new JLabel("ORDER & CLIENT PROFILES");
		lblNewLabel_9.setForeground(new Color(255, 255, 0));
		lblNewLabel_9.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel_9.setBounds(144, 11, 208, 14);
		panelProfiles.add(lblNewLabel_9);
		
		JPanel panelHistories = new JPanel();
		panelHistories.setBackground(new Color(0, 128, 128));
		frmClientRequest.getContentPane().add(panelHistories, "name_36511470292507");
		panelHistories.setLayout(null);
		
		JButton btnNewButton_6 = new JButton("DISCOUNTS HISTORY");
		btnNewButton_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Goes to authentication form and sets a variable to indicate we`re trying to log in this form("DISCOUNTS HISTORY")
				panelMain.setVisible(false);
				panelAuthentication.setVisible(true);
				logIndicator=2;
			}
		});
		btnNewButton_6.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnNewButton_6.setBounds(231, 299, 233, 61);
		panelMain.add(btnNewButton_6);
		
		JTextArea textArea_3 = new JTextArea();
		textArea_3.setEditable(false);
		textArea_3.setBounds(33, 39, 266, 200);
		panelHistories.add(textArea_3);
		
		textField_3 = new JTextField();
		textField_3.setBounds(319, 143, 141, 30);
		panelHistories.add(textField_3);
		textField_3.setColumns(10);
		
		JButton btnNewButton_7 = new JButton("FOR CLIENT VIEW");
		btnNewButton_7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				///////////////////////////////
				String st="";
				String concat="";
				String captions="";
				String clientName="";
				try {
					clientName=textField_3.getText();
					stat=con.prepareStatement("select *from discountHistory where client="+"\""+clientName+"\""+"order by discount;");
					rs = stat.executeQuery();
					while(rs.next()){
				        st=" "+rs.getString(1)+"             "+rs.getString(2);
						concat=concat+"\n"+st;
				        }
					captions=" client\t         discount\n";
					textArea_3.setText(captions+concat);
				} catch (SQLException m) {
					JOptionPane.showMessageDialog(null,m.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
				}
				///////////////////////////////
			}
		});
		btnNewButton_7.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnNewButton_7.setBounds(319, 184, 141, 55);
		panelHistories.add(btnNewButton_7);
		

		
		JButton btnNewButton_8 = new JButton("ALL VIEW");
		btnNewButton_8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				///////////////////////////////
				String st="";
				String concat="";
				String captions="";
				try {
					stat=con.prepareStatement("select *from discountHistory;");
					rs = stat.executeQuery();
					while(rs.next()){
				        st=" "+rs.getString(1)+"             "+rs.getString(2);
						concat=concat+"\n"+st;
				        }
					captions=" client\t         discount\n";
					textArea_3.setText(captions+concat);
				} catch (SQLException m) {
					JOptionPane.showMessageDialog(null,m.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
				}
				///////////////////////////////
			}
		});
		btnNewButton_8.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnNewButton_8.setBounds(319, 39, 141, 55);
		panelHistories.add(btnNewButton_8);
		
		JLabel lblNewLabel_4 = new JLabel("DISCOUNTS HISTORY");
		lblNewLabel_4.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel_4.setForeground(new Color(255, 255, 0));
		lblNewLabel_4.setBounds(82, 14, 162, 14);
		panelHistories.add(lblNewLabel_4);
		
		JButton btnNewButton_9 = new JButton("BACK TO MAIN");
		btnNewButton_9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				///////////////////////////////
				panelMain.setVisible(true);
				panelHistories.setVisible(false);
				///////////////////////////////
			}
		});
		btnNewButton_9.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnNewButton_9.setBounds(33, 283, 187, 63);
		panelHistories.add(btnNewButton_9);
		
		JLabel lblClientId = new JLabel("CLIENT NAME:");
		lblClientId.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));
		lblClientId.setForeground(new Color(255, 255, 0));
		lblClientId.setBounds(319, 121, 84, 14);
		panelHistories.add(lblClientId);
		
		JButton button = new JButton("EXIT");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmClientRequest.dispose();
			}
		});
		button.setFont(new Font("Tahoma", Font.BOLD, 12));
		button.setBounds(237, 281, 223, 64);
		panelHistories.add(button);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(122, 167, 220, 41);
		panelAuthentication.add(passwordField);
		
		textField_4 = new JTextField();
		textField_4.setBounds(122, 102, 220, 29);
		panelAuthentication.add(textField_4);
		textField_4.setColumns(10);
		
		//Log in form for some of the secondary panels
		JButton btnNewButton_11 = new JButton("LOG IN");
		btnNewButton_11.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				String username=textField_4.getText();
				String password=passwordField.getText(); 
				
				//Basically it refers the user to the form indicated by variable(logIndicator) if correct user/pass are present
				if(username.equalsIgnoreCase(Warehouse_Server.user)&&password.equals(Warehouse_Server.password)&&logIndicator==1){
					panelAuthentication.setVisible(false);
					panelProfiles.setVisible(true);
					passwordField.setText("");         
					textField_4.setText("");           
				}else if(username.equalsIgnoreCase(Warehouse_Server.user)&&password.equals(Warehouse_Server.password)&&logIndicator==2){
					panelAuthentication.setVisible(false);
					panelHistories.setVisible(true);
					passwordField.setText("");
					textField_4.setText("");
				}else JOptionPane.showMessageDialog(null,"Wrong username/password!!!", "ERROR", JOptionPane.ERROR_MESSAGE);
			}
		});
		btnNewButton_11.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnNewButton_11.setBounds(71, 242, 164, 66);
		panelAuthentication.add(btnNewButton_11);
		
		JButton btnNewButton_12 = new JButton("BACK TO MAIN");
		btnNewButton_12.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panelMain.setVisible(true);
				panelAuthentication.setVisible(false);
			}
		});
		btnNewButton_12.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnNewButton_12.setBounds(261, 242, 176, 66);
		panelAuthentication.add(btnNewButton_12);
		
		JLabel lblNewLabel_6 = new JLabel("AUTHENTICATION FORM");
		lblNewLabel_6.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel_6.setForeground(new Color(255, 255, 0));
		lblNewLabel_6.setBounds(147, 23, 199, 14);
		panelAuthentication.add(lblNewLabel_6);
		
		JLabel lblNewLabel_7 = new JLabel("USERNAME:");
		lblNewLabel_7.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));
		lblNewLabel_7.setForeground(new Color(255, 255, 0));
		lblNewLabel_7.setBounds(122, 77, 113, 14);
		panelAuthentication.add(lblNewLabel_7);
		
		JLabel lblNewLabel_8 = new JLabel("PASSWORD:");
		lblNewLabel_8.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));
		lblNewLabel_8.setForeground(new Color(255, 255, 0));
		lblNewLabel_8.setBounds(122, 142, 96, 14);
		panelAuthentication.add(lblNewLabel_8);
		


	}
	//Initializes/Fills the ComboBox with the products
	private void cbInit()throws Exception{
        stat = con.prepareStatement("select `group`,`brand`,`model` from products order by `group`;");
        rs = stat.executeQuery();
        prod=new ArrayList<String>();
        
        while (rs.next()){
        prod.add(rs.getString(1)+" "+rs.getString(2)+" "+rs.getString(3));
        }
	}
}
