package app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AccountDao {

	public void createAccount(Connection connection, long userId, String accountNumber) throws SQLException {

		String sql = "INSERT INTO accounts " + "(user_id, account_number, balance, status) " + "VALUES (?, ?, ?, ?)";

		try (PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setLong(1, userId);
			ps.setString(2, accountNumber);
			ps.setDouble(3, 0.0);

			ps.setString(4, "ACTIVE");

			ps.executeUpdate();
		}
	}
}
