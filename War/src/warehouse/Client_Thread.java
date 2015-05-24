package warehouse;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Client_Thread implements Runnable{
	
	DataInputStream din;
	DataOutputStream dout;
	PreparedStatement st;     	 
	ResultSet rs;				
	Connection con;
	
	int price_drop;
	double reduced_price;
	double total_price;
	
public Client_Thread(Socket connection,Connection con)throws Exception {
	super();
	this.con=con;
	din=new DataInputStream(connection.getInputStream());
	dout=new DataOutputStream(connection.getOutputStream());
}

	public synchronized void run(){
		
		try {

			int discount;
			int client;
			int product_id;
			int quantity;
			
		//Receiving data from client//
				 client=din.readInt();
				 product_id=din.readInt();
				 quantity=din.readInt();
				 
		//Checks for enough quantity		 
				 ArrayList<Integer> quan = new ArrayList<Integer>(); 
				 getData(quan,product_id,quantity);
				 if(quan.get(product_id-1)<=0) {dout.writeUTF("\n   PRODUCT OUT OF STOCK!!!");}
				 if(quan.get(product_id-1)<quantity){dout.writeUTF("\n    INSUFFICIENT QUANTITY!!!\n\n       ONLY  "+quan.get(product_id-1)+"  REMAINING...");} 
				 
		//Get the prices, which will drop according to the discount
				 ArrayList<Float> price = new ArrayList<Float>();
				 getData(price);					 
				 
		//Makes the price reductions according to the discount
				switch(quantity){
				case 20:
					discount=1;
					price_drop=5;
					reduced_price=(double)price.get(product_id-1)-0.05*price.get(product_id-1);
					break;
				case 40:
					discount=2;
					price_drop=10;
					reduced_price=(double)price.get(product_id-1)-0.10*price.get(product_id-1);
					break;
				case 60:
					discount=3;
					price_drop=15;
					reduced_price=(double)price.get(product_id-1)-0.15*price.get(product_id-1);
					break;
				case 80:
					discount=4;
					price_drop=20;
					reduced_price=(double)price.get(product_id-1)-0.20*price.get(product_id-1);
					break;
				default:
					discount=0;
					reduced_price=(double)price.get(product_id-1);
				break;
				}
			//Rounding up the result
				reduced_price=(double)Math.round(reduced_price * 100) / 100;
				total_price=(double)Math.round((reduced_price*quantity)*100)/100;
			//Executing the order
				st=con.prepareStatement("CALL order_in("+client+","+product_id+","+quantity+","+discount+","+total_price+")");
				st.execute();
			//Response to client after completed order		
				dout.writeUTF("   ORDER HAS BEEN RECEIVED!\n\n    ORDERED QUANTITY: "+quantity+"\n    DISCOUNT: "+price_drop+"%\n    PRICE(per product): "+reduced_price+" ыт.\n    TOTAL PRICE: "+total_price+" ыт.\n\n   THANK YOU FOR YOUR PURCHASE!");
			}	
				catch (SQLException|IOException e) {
				System.out.println(e.getMessage());
				}	
		}

	
	public void getData(ArrayList<Integer> quan,int product_id,int quantity)throws SQLException{
		 st=con.prepareStatement("select quantity from products;");    
		 rs=st.executeQuery();		
		 while(rs.next()){							
		 quan.add(rs.getInt(1));
		 }
		 
	}
	
	public void getData(ArrayList<Float> price)throws SQLException{ 
		 st=con.prepareStatement("select price from products;");
		 rs=st.executeQuery();
		 while(rs.next()){
		 price.add(rs.getFloat(1));
		 }
	}


}	

