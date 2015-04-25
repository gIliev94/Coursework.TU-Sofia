package warehouse;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;


public class Warehouse_Server {
	
	public static final int port=4444;
	public static String url="jdbc:mysql://localhost:3306/warehouse";
	public static String password="passxmpl";
	public static String user="root";

		
	public static void main(String[] args)throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		//Osushtestvqvane na vruzka s bazata danni
		Connection con=DriverManager.getConnection( url, user, password);
	
		ServerSocket svsock=null;
		Socket connection;
		Thread customer;
		
	try{
			svsock=new ServerSocket(port);
			
			System.out.println("Server is successfully started.");
			
			while(true){
			System.out.println("Waiting for clients to connect...");
			
			connection=svsock.accept();
			
			//Puskane i sinhronizaciq na otdelni nishki za vseki client
			customer=new Thread(new Client_Thread(connection,con));
			customer.start();
			Thread.yield();
			
			System.out.println("Client connected");
			}
		}
			catch(Exception e){
				System.out.println("Can't start server. "+e.getMessage());
				}

	}
}