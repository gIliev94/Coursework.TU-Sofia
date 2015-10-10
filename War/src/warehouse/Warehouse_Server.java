package warehouse;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;

public class Warehouse_Server {

    public static final int port = 4444;
    public static String url = "jdbc:mysql://localhost:3306/warehouse";
    public static String password = "BASKET14fena";
    public static String user = "root";

    public static void main(String[] args) throws Exception {
	// Makes the connection to DB
	Class.forName("com.mysql.jdbc.Driver");
	Connection con = DriverManager.getConnection(url, user, password);
	//

	ServerSocket svsock = null;
	Socket connection;
	Thread customer;

	try {
	    svsock = new ServerSocket(port);

	    System.out.println("Server is successfully started.");

	    while (true) {
		System.out.println("Waiting for clients to connect...");

		connection = svsock.accept();

		customer = new Thread(new Client_Thread(connection, con));
		customer.start();
		Thread.yield();

		System.out.println("Client connected");
	    }
	} catch (Exception e) {
	    System.out.println("Can't start server. " + e.getMessage());
	}

    }
}