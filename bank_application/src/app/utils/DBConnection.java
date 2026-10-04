package app.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

	private final static String URL = "jdbc:mysql://localhost:3306/bank_db";
	private final static String USERNAME = "root";
	private final static String PASSWORD = "Chandu@2001";

	public static Connection getConnection() {

		try {
			return DriverManager.getConnection(URL, USERNAME, PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;

	}

}
