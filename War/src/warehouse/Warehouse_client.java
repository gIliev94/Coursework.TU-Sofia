package warehouse;

import java.awt.CardLayout;
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
import java.sql.ResultSetMetaData;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

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
	static String host="localhost";
	private JTable table_1;
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
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() throws Exception{
		frmClientRequest = new JFrame();
		frmClientRequest.setResizable(false);
		frmClientRequest.getContentPane().setBackground(new Color(0, 128, 128));
		frmClientRequest.setTitle("CLIENT REQUEST");
		frmClientRequest.setBounds(100, 100, 493, 308);
		frmClientRequest.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmClientRequest.getContentPane().setLayout(new CardLayout(0, 0));
		
		////////////////////////////////////////////////////////
		connection=new Socket(host,Warehouse_Server.port);
		din=new DataInputStream(connection.getInputStream());
		dout=new DataOutputStream(connection.getOutputStream());
		////////////////////////////////////////////////////////
		
		JPanel panelMain = new JPanel();
		frmClientRequest.getContentPane().add(panelMain, "name_30744167895812");
		panelMain.setBackground(new Color(0, 128, 128));
		panelMain.setLayout(null);
		
		JPanel panelCatalog = new JPanel();
		frmClientRequest.getContentPane().add(panelCatalog, "name_30746246934744");
		panelCatalog.setBackground(new Color(0, 128, 128));
		panelCatalog.setLayout(null);
		
		JButton btnNewButton_3 = new JButton("BACK");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				///////////////////////////////
				panelCatalog.setVisible(false);
				panelMain.setVisible(true);
				///////////////////////////////
			}
		});
		btnNewButton_3.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnNewButton_3.setBounds(176, 234, 137, 35);
		panelCatalog.add(btnNewButton_3);
		
		JLabel lblNewLabel_2 = new JLabel("PRODUCT CATALOG:");
		lblNewLabel_2.setForeground(Color.YELLOW);
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel_2.setBounds(157, 11, 156, 14);
		panelCatalog.add(lblNewLabel_2);
		
		///////////////////////////////
		DefaultTableModel dtm=FillTable(table_1);
		table_1 = new JTable(dtm);
		///////////////////////////////
		table_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		table_1.setEnabled(false);
		table_1.setRowSelectionAllowed(false);
		table_1.setBounds(25, 36, 438, 182);
		panelCatalog.add(table_1);
		
		JLabel lblNewLabel = new JLabel("Client ID:");
		lblNewLabel.setForeground(Color.YELLOW);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel.setBounds(43, 43, 59, 27);
		panelMain.add(lblNewLabel);
		
		JLabel lblProductId = new JLabel("Product ID:");
		lblProductId.setForeground(Color.YELLOW);
		lblProductId.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblProductId.setBounds(39, 101, 71, 27);
		panelMain.add(lblProductId);
		
		JLabel lblQuantity = new JLabel("Quantity:");
		lblQuantity.setForeground(Color.YELLOW);
		lblQuantity.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblQuantity.setBounds(43, 154, 59, 27);
		panelMain.add(lblQuantity);
		
		textArea = new JTextArea();
		textArea.setBackground(new Color(255, 255, 255));
		textArea.setEditable(false);
		textArea.setBounds(219, 43, 233, 147);
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
				btnNewButton.setEnabled(false);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,"Invalid data input!\nPlease enter correct values...", "ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnNewButton.setBounds(43, 201, 159, 61);
		panelMain.add(btnNewButton);
		
		textField = new JTextField();
		textField.setBounds(112, 47, 86, 20);
		panelMain.add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(112, 105, 86, 20);
		panelMain.add(textField_1);
		
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(112, 161, 86, 20);
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
		btnNewButton_1.setBounds(219, 201, 233, 61);
		panelMain.add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton("VIEW CATALOG");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panelMain.setVisible(false);
				panelCatalog.setVisible(true);
			}
		});
		btnNewButton_2.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnNewButton_2.setBounds(40, 9, 158, 27);
		panelMain.add(btnNewButton_2);

	}
	
	//Metod za izgrajdane(modela) na tablicata
	public static DefaultTableModel FillTable(JTable table) throws Exception{
	    	Class.forName("com.mysql.jdbc.Driver");
	    	Connection con=DriverManager.getConnection( Warehouse_Server.url, Warehouse_Server.user,Warehouse_Server.password);
	        PreparedStatement stat = con.prepareStatement("select `id`,`group`,`brand`,`model`,`price` from products order by `group`;");
	        ResultSet rs = stat.executeQuery();
	        ResultSetMetaData metaData = rs.getMetaData();

	        Vector<String> columnNames = new Vector<String>();
	        int columnCount = metaData.getColumnCount();
	        for (int column = 1; column <= columnCount; column++) {
	            columnNames.add(metaData.getColumnName(column));
	        }

	        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
	        while (rs.next()) {
	            Vector<Object> vector = new Vector<Object>();
	            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
	                vector.add(rs.getObject(columnIndex));
	            }
	            data.add(vector);
	        }
        return new DefaultTableModel(data, columnNames);
	}
}
