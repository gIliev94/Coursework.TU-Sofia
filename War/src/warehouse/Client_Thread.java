package warehouse;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
		
			
		//Poluchavane na dannite ot clienta
				 client=din.readInt();
				 product_id=din.readInt();
				 quantity=din.readInt();
			
		//Zarejdane na stoinostite za nalichnite kolichestva i proverka za lipsa\nedostig na takiva	 
				 ArrayList<Integer> quan = new ArrayList<Integer>(); 
				 getData(quan,product_id,quantity);
				 if(quan.get(product_id-1)<=0) {dout.writeUTF("\n   PRODUCT OUT OF STOCK!!!");
				 return;}
				 if(quan.get(product_id-1)<quantity){dout.writeUTF("\n    INSUFFICIENT QUANTITY!!!\n\n       ONLY  "+quan.get(product_id-1)+"  REMAINING...");
				 return;} 
				 
		//Zarejdane na stoinostite za nastoqshtite ceni, koito posle da namaleqt spored otstupkite
				 ArrayList<Float> price = new ArrayList<Float>();
				 getData(price);
				 
		//Proverka za otstupka spored zakupenoto kolichestvo
				switch(quantity){
				case 20:
					discount=1;
					price_drop=5;
					//Namalqvane na cenata spored otstupkata
					reduced_price=(double)price.get(product_id-1)-0.05*price.get(product_id-1);
					//Namalqvane na nalichnite v sklada kolichestva sus kolichestvoto na poruchkata
					st=con.prepareStatement("update products set quantity=quantity-"+quantity+" where id="+product_id+";");
					st.executeUpdate();
					break;
				case 40:
					discount=2;
					price_drop=10;
					reduced_price=(double)price.get(product_id-1)-0.10*price.get(product_id-1);
					st=con.prepareStatement("update products set quantity=quantity-"+quantity+" where id="+product_id+";");
					st.executeUpdate();
					break;
				case 60:
					discount=3;
					price_drop=15;
					reduced_price=(double)price.get(product_id-1)-0.15*price.get(product_id-1);
					st=con.prepareStatement("update products set quantity=quantity-"+quantity+" where id="+product_id+";");
					st.executeUpdate();
					break;
				case 80:
					discount=4;
					price_drop=20;
					reduced_price=(double)price.get(product_id-1)-0.20*price.get(product_id-1);
					st=con.prepareStatement("update products set quantity=quantity-"+quantity+" where id="+product_id+";");
					st.executeUpdate();
					break;
				default:
					discount=0;
					reduced_price=(double)price.get(product_id-1);
					st=con.prepareStatement("update products set quantity=quantity-"+quantity+" where id="+product_id+";");
					st.executeUpdate();
				break;
				}
			//Zakruglenie do 2-ri znak
				reduced_price=(double)Math.round(reduced_price * 100) / 100;
				total_price=(double)Math.round((reduced_price*quantity)*100)/100;
			//Izgotvqne i izpulnenie na MySQL zaqvkata za izpulnenie na poruchkata
				st=con.prepareStatement("insert into `warehouse`.`orders`(`customer_id` ,`product_id` ,`order_quantity`,`discount_id`,`reduced_price`) values("+client+","+product_id+","+quantity+","+discount+","+reduced_price+")");
				st.executeUpdate();
			//Vrushtane na otgovor kum clienta		
				dout.writeUTF("   ORDER HAS BEEN RECEIVED!\n\n    ORDERED QUANTITY: "+quantity+"\n    DISCOUNT: "+price_drop+"%\n    PRICE(per product): "+reduced_price+" ыт.\n    TOTAL PRICE: "+total_price+" ыт.\n\n   THANK YOU FOR YOUR PURCHASE!");
			}	
				catch (Exception e) {
				System.out.println(e.getMessage());
				}	
		}
	
	public void getData(ArrayList<Integer> quan,int product_id,int quantity)throws Exception{
		 st=con.prepareStatement("select quantity from products;");
		 rs=st.executeQuery();
		 while(rs.next()){
		 quan.add(rs.getInt(1));
		 }
		 
	}
	
	public void getData(ArrayList<Float> price)throws Exception{ 
		 st=con.prepareStatement("select price from products;");
		 rs=st.executeQuery();
		 while(rs.next()){
		 price.add(rs.getFloat(1));
		 }
	}


}	

