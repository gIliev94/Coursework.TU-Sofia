package warehouse.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import warehouse.database.DatabaseConnector;
import warehouse.server.WarehouseServer;
import warehouse.utilities.StringConstants;

/**
 * Manages a separate path of execution for every client`s order.
 * 
 * @author Georgi Iliev
 *
 */
public class ClientThread implements Runnable {

    private DataInputStream requestStream;
    private DataOutputStream responseStream;

    private Connection dbConnection;
    private PreparedStatement request;

    public ClientThread(Socket clientConnection) throws ClassNotFoundException, SQLException, IOException {
	this.dbConnection = DatabaseConnector.getInstance().getConnection();
	requestStream = new DataInputStream(clientConnection.getInputStream());
	responseStream = new DataOutputStream(clientConnection.getOutputStream());
    }

    public synchronized void run() {

	try {

	    int client = 0;
	    int product = 0;
	    int quantity = 0;

	    client = requestStream.readInt();
	    product = requestStream.readInt();
	    quantity = requestStream.readInt();

	    warnOnUnavailability(product, quantity);

	    List<Object> prices = new ArrayList<>();
	    DatabaseConnector.extractData(prices, "price");

	    Order order = new Order();
	    order.setClient(client);
	    order.setProductId(product);
	    order.setQuantity(quantity);
	    order.setPrices(prices);

	    order = OrderProcessor.getProcessedOrder(order);

	    executeOrder(order);

	    sendOrderDetails(order);

	} catch (SQLException sqle) {
	    JOptionPane.showMessageDialog(null, "Problem accessing DB: " + sqle.getLocalizedMessage(), "ERROR",
		    JOptionPane.ERROR_MESSAGE);
	    WarehouseServer.LOG.error("Problem accessing DB: ", sqle);
	} catch (IOException e) {
	    JOptionPane.showMessageDialog(null, "Client disconnected.", "INFO", JOptionPane.INFORMATION_MESSAGE);
	    WarehouseServer.LOG.info("Client disconnected.");
	} catch (ClassNotFoundException e) {
	    JOptionPane.showMessageDialog(null, e.getLocalizedMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
	    WarehouseServer.LOG.error(e.getLocalizedMessage());
	}
    }

    private void warnOnUnavailability(int product_id, int quantity) throws SQLException, IOException,
	    ClassNotFoundException {
	List<Object> quantities = new ArrayList<>();

	DatabaseConnector.extractData(quantities, "quantity");

	int availableQuantity = (int) quantities.get(product_id - 1);

	if (availableQuantity <= 0) {
	    responseStream.writeUTF(StringConstants.LINE + "   PRODUCT OUT OF STOCK!!!");
	}
	if (availableQuantity < quantity) {
	    responseStream.writeUTF(StringConstants.LINE + "    INSUFFICIENT QUANTITY!!!" + StringConstants.LINE
		    + StringConstants.LINE + "       ONLY  " + availableQuantity + "  REMAINING...");
	}
    }

    private void executeOrder(Order order) throws SQLException {
	request = dbConnection.prepareStatement("CALL order_in(" + order.getClient() + "," + order.getProductId() + ","
		+ order.getQuantity() + "," + order.getDiscount() + "," + order.getTotalPrice() + ")");
	request.execute();
    }

    private void sendOrderDetails(Order order) throws IOException {
	responseStream.writeUTF("   ORDER HAS BEEN RECEIVED!" + StringConstants.LINE + StringConstants.LINE
		+ "    ORDERED QUANTITY: " + order.getQuantity() + StringConstants.LINE + "    DISCOUNT: "
		+ order.getPriceDrop() + "%" + StringConstants.LINE + "    PRICE(per product): $"
		+ order.getReducedPrice() + StringConstants.LINE + "    TOTAL PRICE: $" + order.getTotalPrice()
		+ StringConstants.LINE + StringConstants.LINE + "   THANK YOU FOR YOUR PURCHASE!");
    }

}