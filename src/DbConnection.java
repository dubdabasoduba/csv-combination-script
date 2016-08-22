import java.sql.Connection;
import java.sql.DriverManager;

public class DbConnection {

	public static Connection getConnection() {
		Connection conn = null;
		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "kapsowar2";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "root";
		String password = "@Mwal1mu1";
		try {
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url + dbName, userName, password);
			System.out.println("Connected to the database");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return conn;
	}

}
