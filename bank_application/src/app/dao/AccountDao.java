package app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import app.entity.Account;
import app.entity.Customer;

public class AccountDao {

	public void createAccount(Connection connection, long userId, String accountNumber) throws SQLException {
		String sql = "INSERT INTO accounts (user_id, account_number, balance, status) VALUES (?, ?, ?, ?)";

		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setLong(1, userId);
			ps.setString(2, accountNumber);
			ps.setDouble(3, 0.0);
			ps.setString(4, "ACTIVE");
			ps.executeUpdate();
		}
	}

	private Account mapAccount(ResultSet rs) throws SQLException {
		Account a = new Account();
		a.setAccountId(rs.getLong("account_id"));
		a.setUserId(rs.getLong("user_id"));
		a.setAccountNumber(rs.getString("account_number"));
		a.setBalance(rs.getDouble("balance"));
		a.setStatus(rs.getString("status"));
		return a;
	}

	public Account findByUserId(Connection connection, long userId) throws SQLException {
		String sql = "SELECT * FROM accounts WHERE user_id = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setLong(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? mapAccount(rs) : null;
			}
		}
	}

	// locks the row until commit/rollback
	public Account findByAccountNumberForUpdate(Connection connection, String accountNumber) throws SQLException {
		String sql = "SELECT * FROM accounts WHERE account_number = ? FOR UPDATE";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, accountNumber);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? mapAccount(rs) : null;
			}
		}
	}

	public void updateBalance(Connection connection, String accountNumber, double newBalance) throws SQLException {
		String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setDouble(1, newBalance);
			ps.setString(2, accountNumber);
			ps.executeUpdate();
		}
	}

	public int updateStatus(Connection connection, String accountNumber, String status) throws SQLException {
		String sql = "UPDATE accounts SET status = ? WHERE account_number = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, status);
			ps.setString(2, accountNumber);
			return ps.executeUpdate();
		}
	}

	public List<Customer> findAllCustomers(Connection connection) throws SQLException {
		String sql = "SELECT u.full_name, u.email, u.phone, u.role, "
				+ "a.account_number, a.balance, a.status "
				+ "FROM users u JOIN accounts a ON u.user_id = a.user_id ORDER BY u.user_id";

		List<Customer> list = new ArrayList<>();
		try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				list.add(new Customer(rs.getString("full_name"), rs.getString("email"), rs.getString("phone"),
						null, rs.getString("role"), rs.getString("account_number"), rs.getDouble("balance"),
						rs.getString("status")));
			}
		}
		return list;
	}
}