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
import java.util.List;

public class Client_Thread implements Runnable {

    DataInputStream din;
    DataOutputStream dout;

    Connection dbConnection;
    PreparedStatement st;
    ResultSet rs;

    public Client_Thread(Socket clientConnection)
	    throws ClassNotFoundException, SQLException, IOException {
	this.dbConnection = new DataLayer().getConnection();
	din = new DataInputStream(clientConnection.getInputStream());
	dout = new DataOutputStream(clientConnection.getOutputStream());
    }

    public synchronized void run() {

	try {

	    int price_drop = 0;
	    double reduced_price = 0;
	    double total_price = 0;
	    int discount = 0;
	    int client = 0;
	    int product_id = 0;
	    int quantity = 0;

	    // Receiving data from client//
	    client = din.readInt();
	    product_id = din.readInt();
	    quantity = din.readInt();

	    // Checks for enough quantity
	    warnOnUnavailability(product_id, quantity);

	    // Get the prices, which will drop according to the discount
	    List<Float> prices = getPrices();

	    // Makes the price reductions according to the discount
	    switch (quantity) {
	    case 20:
		discount = 1;
		price_drop = 5;
		reduced_price = (double) prices.get(product_id - 1) - 0.05
			* prices.get(product_id - 1);
		break;
	    case 40:
		discount = 2;
		price_drop = 10;
		reduced_price = (double) prices.get(product_id - 1) - 0.10
			* prices.get(product_id - 1);
		break;
	    case 60:
		discount = 3;
		price_drop = 15;
		reduced_price = (double) prices.get(product_id - 1) - 0.15
			* prices.get(product_id - 1);
		break;
	    case 80:
		discount = 4;
		price_drop = 20;
		reduced_price = (double) prices.get(product_id - 1) - 0.20
			* prices.get(product_id - 1);
		break;
	    default:
		discount = 0;
		price_drop = 0;
		reduced_price = (double) prices.get(product_id - 1);
		break;
	    }
	    // Rounding up the result
	    reduced_price = (double) Math.round(reduced_price * 100) / 100;
	    total_price = (double) Math.round((reduced_price * quantity) * 100) / 100;
	    // Executing the order
	    executeOrder(total_price, discount, client, product_id, quantity);
	    // Response to client after completed order
	    sendOrderDetails(price_drop, reduced_price, total_price, quantity);

	} catch (SQLException | IOException e) {
	    System.out.println(e.getMessage());
	} finally {
	    try {
		din.close();
		dout.close();
	    } catch (IOException e) {
		System.out.println(e.getMessage());
	    }
	}
    }

    private List<Float> getPrices() throws SQLException {
	List<Float> priceList = new ArrayList<>();
	getData(priceList);
	return priceList;
    }

    private void sendOrderDetails(int price_drop, double reduced_price,
	    double total_price, int quantity) throws IOException {
	dout.writeUTF("   ORDER HAS BEEN RECEIVED!\n\n    ORDERED QUANTITY: "
		+ quantity + "\n    DISCOUNT: " + price_drop
		+ "%\n    PRICE(per product): " + reduced_price
		+ " ыт.\n    TOTAL PRICE: " + total_price
		+ " ыт.\n\n   THANK YOU FOR YOUR PURCHASE!");
    }

    private void executeOrder(double total_price, int discount, int client,
	    int product_id, int quantity) throws SQLException {
	st = dbConnection.prepareStatement("CALL order_in(" + client + ","
		+ product_id + "," + quantity + "," + discount + ","
		+ total_price + ")");
	st.execute();
    }

    private void warnOnUnavailability(int product_id, int quantity)
	    throws SQLException, IOException {
	ArrayList<Integer> quan = new ArrayList<>();
	getData(quan, product_id, quantity);
	if (quan.get(product_id - 1) <= 0) {
	    dout.writeUTF("\n   PRODUCT OUT OF STOCK!!!");
	}
	if (quan.get(product_id - 1) < quantity) {
	    dout.writeUTF("\n    INSUFFICIENT QUANTITY!!!\n\n       ONLY  "
		    + quan.get(product_id - 1) + "  REMAINING...");
	}
    }

    public void getData(List<Integer> quan, int product_id, int quantity)
	    throws SQLException {
	st = dbConnection.prepareStatement("select quantity from products;");
	rs = st.executeQuery();
	while (rs.next()) {
	    quan.add(rs.getInt(1));
	}
    }

    public void getData(List<Float> price) throws SQLException {
	st = dbConnection.prepareStatement("select price from products;");
	rs = st.executeQuery();
	while (rs.next()) {
	    price.add(rs.getFloat(1));
	}
    }
}
