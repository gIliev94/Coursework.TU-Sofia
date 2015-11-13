package warehouse;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

public class DataLayer {

    public static final String url = "jdbc:mysql://localhost:3306/warehouse";
    public static final String password = "BASKET14fena";
    public static final String user = "root";

    private Connection connection;

    public DataLayer() throws SQLException, ClassNotFoundException {
	Class.forName("com.mysql.jdbc.Driver");
	connection = DriverManager.getConnection(url, user, password);
    }

    public Connection getConnection() {
	return connection;
    }
}
