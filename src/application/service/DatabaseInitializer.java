package application.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

	public static void main(String[] args) {
		createDatabaseAndTables();
	}

	public static void createDatabaseAndTables() {

		String url = "jdbc:mysql://localhost:3306/";
		String user = "root";
		String password = "Password123$";
		String databaseName = "shc";

		Connection connection = null;
		Statement statement = null;

		try {
			connection = DriverManager.getConnection(url, user, password);

			statement = connection.createStatement();
			statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + databaseName);

			statement.executeUpdate("USE " + databaseName);

			// Create Product table if it doesn't exist
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS Product(" + "id INT AUTO_INCREMENT PRIMARY KEY,"
					+ "product_collection VARCHAR(255)," + "product_type ENUM('TYPE1', 'TYPE2')," + "gold FLOAT,"
					+ "silver FLOAT," + "price FLOAT," + "is_deleted BOOLEAN,"
					+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
					+ "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)");

			// Create Transaction table if it doesn't exist
			//statement.executeUpdate("DROP TABLE IF EXISTS Transaction"); //uncomment this if needed.
			//Had to reset the table so I could input the 'FIX' ENUM into the status column
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS Transaction(" + "id INT AUTO_INCREMENT PRIMARY KEY,"
					+ "product_id INT," + "quantity INTEGER," + "status ENUM('SOLD', 'ADDED', 'REFUNDED', 'FIX ADD', 'FIX SOLD'),"
					+ "created_at DATETIME DEFAULT CURRENT_TIMESTAMP,"
					+ "FOREIGN KEY (product_id) REFERENCES Product(id))");

			// Create PriceArchive table if it doesn't exist
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS PriceArchive(" + "id INT AUTO_INCREMENT PRIMARY KEY,"
					+ "product_id INT," + "old_price FLOAT," + "new_price FLOAT,"
					+ "created_at DATETIME DEFAULT CURRENT_TIMESTAMP,"
					+ "FOREIGN KEY (product_id) REFERENCES Product(id))");

			statement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
