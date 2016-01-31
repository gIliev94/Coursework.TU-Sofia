package warehouse.database;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Singleton connector for the database.
 * 
 * @author Georgi Iliev
 *
 */
public class DatabaseConnector {

    private static final DatabaseConnector DATABASE;

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
	return DriverManager.getConnection(DBConstants.URL, DBConstants.USER, DBConstants.PASSWORD);
    }
}
