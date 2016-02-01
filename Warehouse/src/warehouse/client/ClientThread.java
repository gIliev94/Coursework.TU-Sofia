package warehouse.client;

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

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import warehouse.database.DatabaseConnector;
import warehouse.server.WarehouseServer;
import warehouse.utilities.Order;
import warehouse.utilities.OrderProcessor;

/**
 * Manages a separate path of execution for every client`s order.
 * 
 * @author Georgi Iliev
 *
 */
public class ClientThread implements Runnable {

    private static final Logger LOG = Logger.getLogger(WarehouseServer.class);

    DataInputStream requestStream;
    DataOutputStream responseStream;

    Connection dbConnection;
    PreparedStatement request;
    ResultSet response;

    public ClientThread(Socket clientConnection) throws ClassNotFoundException, SQLException, IOException {
	this.dbConnection = DatabaseConnector.getInstance().getConnection();
	requestStream = new DataInputStream(clientConnection.getInputStream());
	responseStream = new DataOutputStream(clientConnection.getOutputStream());
    }

    public synchronized void run() {

	try {

	    int client = 0;
	    int productId = 0;
	    int quantity = 0;

	    client = requestStream.readInt();
	    productId = requestStream.readInt();
	    quantity = requestStream.readInt();

	    warnOnUnavailability(productId, quantity);

	    List<Float> prices = getPrices();

	    Order order = new Order();
	    order.setClient(client);
	    order.setProductId(productId);
	    order.setQuantity(quantity);
	    order.setPrices(prices);

	    order = OrderProcessor.getProcessedOrder(order);

	    executeOrder(order);

	    sendOrderDetails(order);

	} catch (SQLException sqle) {
	    LOG.error("Problem accessing DB: ", sqle);
	    JOptionPane.showMessageDialog(null, "Problem accessing DB: " + sqle.getMessage(), "ERROR",
		    JOptionPane.ERROR_MESSAGE);
	} catch (IOException e) {
	    LOG.info("Client disconnected.");
	    JOptionPane.showMessageDialog(null, "Client disconnected.", "INFO", JOptionPane.INFORMATION_MESSAGE);
	}
    }

    private List<Float> getPrices() throws SQLException {
	List<Float> priceList = new ArrayList<>();
	getData(priceList);
	return priceList;
    }

    private void executeOrder(Order order) throws SQLException {
	request = dbConnection.prepareStatement("CALL order_in(" + order.getClient() + "," + order.getProductId() + ","
		+ order.getQuantity() + "," + order.getDiscount() + "," + order.getTotalPrice() + ")");
	request.execute();
    }

    private void sendOrderDetails(Order order) throws IOException {
	responseStream.writeUTF("   ORDER HAS BEEN RECEIVED!\n\n    ORDERED QUANTITY: " + order.getQuantity()
		+ "\n    DISCOUNT: " + order.getPriceDrop() + "%\n    PRICE(per product): " + order.getReducedPrice()
		+ " ыт.\n    TOTAL PRICE: " + order.getTotalPrice() + " ыт.\n\n   THANK YOU FOR YOUR PURCHASE!");
    }

    private void warnOnUnavailability(int product_id, int quantity) throws SQLException, IOException {
	ArrayList<Integer> quantities = new ArrayList<>();
	getData(quantities, product_id, quantity);
	if (quantities.get(product_id - 1) <= 0) {
	    responseStream.writeUTF("\n   PRODUCT OUT OF STOCK!!!");
	    responseStream.flush();
	}
	if (quantities.get(product_id - 1) < quantity) {
	    responseStream.writeUTF("\n    INSUFFICIENT QUANTITY!!!\n\n       ONLY  " + quantities.get(product_id - 1)
		    + "  REMAINING...");
	}
    }

    private void getData(List<Integer> quantities, int product_id, int quantity) throws SQLException {
	request = dbConnection.prepareStatement("select quantity from products;");
	response = request.executeQuery();
	while (response.next()) {
	    quantities.add(response.getInt(1));
	}
    }

    private void getData(List<Float> prices) throws SQLException {
	request = dbConnection.prepareStatement("select price from products;");
	response = request.executeQuery();
	while (response.next()) {
	    prices.add(response.getFloat(1));
	}
    }
}
