package app.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import app.entity.Transaction;

public class TransactionDao {

	public void insert(Connection connection, Transaction t) throws SQLException {
		String sql = "INSERT INTO transactions (account_number, txn_type, amount, balance_after, other_account) "
				+ "VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, t.getAccountNumber());
			ps.setString(2, t.getTxnType());
			ps.setDouble(3, t.getAmount());
			ps.setDouble(4, t.getBalanceAfter());
			ps.setString(5, t.getOtherAccount());
			ps.executeUpdate();
		}
	}

	public List<Transaction> findByAccount(Connection connection, String accountNumber, int limit)
			throws SQLException {
		String sql = "SELECT * FROM transactions WHERE account_number = ? ORDER BY txn_id DESC LIMIT ?";
		List<Transaction> list = new ArrayList<>();
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, accountNumber);
			ps.setInt(2, limit);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Transaction t = new Transaction();
					t.setTxnId(rs.getLong("txn_id"));
					t.setAccountNumber(rs.getString("account_number"));
					t.setTxnType(rs.getString("txn_type"));
					t.setAmount(rs.getDouble("amount"));
					t.setBalanceAfter(rs.getDouble("balance_after"));
					t.setOtherAccount(rs.getString("other_account"));
					t.setCreatedAt(rs.getTimestamp("created_at"));
					list.add(t);
				}
			}
		}
		return list;
	}
}
