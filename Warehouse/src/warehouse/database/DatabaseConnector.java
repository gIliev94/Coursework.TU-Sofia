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
     * @return
     */
    public static DatabaseConnector getInstance() {
	return DATABASE;
    }

    /**
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Connection getConnection() throws ClassNotFoundException, SQLException {
	Class.forName("com.mysql.jdbc.Driver");
	return DriverManager.getConnection(DBConstants.URL, DBConstants.USER, DBConstants.PASSWORD);
    }
}
