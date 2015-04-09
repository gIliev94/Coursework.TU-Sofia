package warehouse;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.Color;

import javax.swing.UIManager;


public class Warehouse_client {

	private JFrame frmClientRequest;
	private JTextArea textArea;
	private JLabel lblNewLabel_1;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	
	///////////////////////////////
	private DataInputStream din;
	private DataOutputStream dout;
	private Socket connection;
	static int port=4444;
	static String host="localhost";
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
	public Warehouse_client() throws Exception{
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() throws Exception{
		frmClientRequest = new JFrame();
		frmClientRequest.getContentPane().setBackground(new Color(0, 128, 128));
		frmClientRequest.setResizable(false);
		frmClientRequest.setTitle("CLIENT REQUEST");
		frmClientRequest.setBounds(100, 100, 493, 308);
		frmClientRequest.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmClientRequest.getContentPane().setLayout(null);
		
		////////////////////////////////////////////////////////
		connection=new Socket(host,port);
		din=new DataInputStream(connection.getInputStream());
		dout=new DataOutputStream(connection.getOutputStream());
		////////////////////////////////////////////////////////
		
		JLabel lblNewLabel = new JLabel("Client ID:");
		lblNewLabel.setForeground(Color.YELLOW);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel.setBounds(43, 43, 59, 27);
		frmClientRequest.getContentPane().add(lblNewLabel);
		
		JLabel lblProductId = new JLabel("Product ID:");
		lblProductId.setForeground(Color.YELLOW);
		lblProductId.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblProductId.setBounds(31, 92, 71, 27);
		frmClientRequest.getContentPane().add(lblProductId);
		
		JLabel lblQuantity = new JLabel("Quantity:");
		lblQuantity.setForeground(Color.YELLOW);
		lblQuantity.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblQuantity.setBounds(43, 144, 59, 27);
		frmClientRequest.getContentPane().add(lblQuantity);
		
		textArea = new JTextArea();
		textArea.setBackground(new Color(255, 255, 255));
		textArea.setEditable(false);
		textArea.setBounds(219, 46, 233, 122);
		frmClientRequest.getContentPane().add(textArea);
		
		lblNewLabel_1 = new JLabel("YOUR ORDER:");
		lblNewLabel_1.setForeground(Color.YELLOW);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel_1.setBounds(284, 21, 100, 14);
		frmClientRequest.getContentPane().add(lblNewLabel_1);
		
		JButton btnNewButton = new JButton("SUBMIT");
		btnNewButton.setForeground(UIManager.getColor("Button.foreground"));
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnNewButton.setBackground(UIManager.getColor("Button.background"));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
		try {
			//Vuvejdane na dannite ot clienta
			int client=Integer.parseInt(textField.getText());
			int product=Integer.parseInt(textField_1.getText());
			int quantity=Integer.parseInt(textField_2.getText());
			String msg;
			
			//Izprashtane kum servera(t.e. kum negovata nishka)
				dout.writeInt(client);
				dout.writeInt(product);
				dout.writeInt(quantity);
			//Poluchavane na otgovor i pokazvaneto mu
				msg = din.readUTF();
				textArea.setText(msg);
				
			} catch (Exception e) {
				textArea.setText("Invalid data input!\nPlease enter correct values.");
				}
			}
		});
		btnNewButton.setBounds(39, 191, 159, 61);
		frmClientRequest.getContentPane().add(btnNewButton);
		
		textField = new JTextField();
		textField.setBounds(112, 47, 86, 20);
		frmClientRequest.getContentPane().add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(112, 96, 86, 20);
		frmClientRequest.getContentPane().add(textField_1);
		
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(112, 148, 86, 20);
		frmClientRequest.getContentPane().add(textField_2);
		
		JButton btnNewButton_1 = new JButton("EXIT");
		btnNewButton_1.setForeground(UIManager.getColor("Button.foreground"));
		btnNewButton_1.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnNewButton_1.setBackground(UIManager.getColor("Button.background"));
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frmClientRequest.dispose();
			}
		});
		btnNewButton_1.setBounds(219, 191, 233, 61);
		frmClientRequest.getContentPane().add(btnNewButton_1);
		
	}
}
