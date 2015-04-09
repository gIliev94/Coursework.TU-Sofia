package warehouse;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;



public class Client_Thread implements Runnable{
	DataInputStream din;
	DataOutputStream dout;
	PreparedStatement st;
	Connection con;

public Client_Thread(Socket connection,Connection con)throws Exception {
	super();
	this.con=con;
	din=new DataInputStream(connection.getInputStream());
	dout=new DataOutputStream(connection.getOutputStream());
}
	
	public synchronized void run(){
		
		try {

			String price_drop;
			int discount;
			int client;
			int product_id;
			int quantity;
			
		//Poluchavane na dannite ot clienta
				 client=din.readInt();
				 product_id=din.readInt();
				 quantity=din.readInt();
		
		//Proverka za otstupka spored zakupenoto kolichestvo
				switch(quantity){
				case 20:
					discount=1;
					price_drop="5%";
					break;
				case 40:
					discount=2;
					price_drop="10%";
					break;
				case 60:
					discount=3;
					price_drop="15%";
					break;
				case 80:
					discount=4;
					price_drop="20%";
					break;
				default:
					discount=0;
					price_drop="0%";
				break;
				}
			//Izgotvqne i izpulnenie na MySQL zaqvkata	
				st=con.prepareStatement("insert into `warehouse`.`orders`(`customer_id` ,`product_id` ,`order_quantity`,`discount_id`) values("+client+","+product_id+","+quantity+","+discount+")");
				st.executeUpdate();
			//Vrushtane na otgovor kum clienta		
				dout.writeUTF("\n   ORDER HAS BEEN RECEIVED!\n\n    ORDERED QUANTITY: "+quantity+"\n    DISCOUNT: "+price_drop+"\n\n   THANK YOU FOR YOUR PURCHASE!");
			}	
				catch (Exception e) {
				System.out.println(e.getMessage());
				}	
		
		}
}	

