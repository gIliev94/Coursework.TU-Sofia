package warehouse.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Singleton connector for the database.
 * 
 * @author Georgi Iliev
 *
 */
public class DatabaseConnector {

    private static final DatabaseConnector DATABASE;
    private static Connection CONNECTION;

    private DatabaseConnector() {
    }

    static {
	DATABASE = new DatabaseConnector();
    }

    /**
     * 
     * @return The instance of the database connector.
     */
    public static DatabaseConnector getInstance() {
	return DATABASE;
    }

    /**
     * 
     * @return The connection to the actual database.
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Connection getConnection() throws ClassNotFoundException, SQLException {
	CONNECTION = DriverManager.getConnection(DBConstants.URL, DBConstants.USER, DBConstants.PASSWORD);
	return CONNECTION;
    }

    /**
     * Extracts all data from the database for the parameter column.
     * 
     * @param data
     *            - list to store the data in.
     * @param column
     *            - column for which the data will be extracted.
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static void extractData(List<Object> data, String column) throws SQLException, ClassNotFoundException {
	PreparedStatement request = CONNECTION.prepareStatement("select " + column + " from products;");
	ResultSet response = request.executeQuery();

	while (response.next()) {
	    switch (column) {
	    case "quantity":
		data.add(response.getInt(column));
		break;
	    case "price":
		data.add(response.getDouble(column));
		break;
	    default:
		throw new SQLException("No such column in DB!");
	    }
	}
    }

}
