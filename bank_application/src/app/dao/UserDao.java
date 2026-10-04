package app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import app.entity.User;

public class UserDao {

	public boolean existsByEmail(Connection connection, String email) throws SQLException {

		String sql = "SELECT user_id FROM users " + "WHERE email = ?";

		try (PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setString(1, email);

			try (ResultSet rs = ps.executeQuery()) {

				return rs.next();
			}
		}
	}

	public long createUser(Connection connection, User user) throws SQLException {

		String sql = "INSERT INTO users " + "(full_name, email, phone, password, role) " + "VALUES (?, ?, ?, ?, ?)";

		try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

			ps.setString(1, user.getFullName());
			ps.setString(2, user.getEmail());
			ps.setString(3, user.getPhone());
			ps.setString(4, user.getPassword());
			ps.setString(5, user.getRole());

			ps.executeUpdate();

			try (ResultSet rs = ps.getGeneratedKeys()) {

				if (rs.next()) {
					return rs.getLong(1);
				}
			}
		}

		throw new SQLException("User ID was not generated.");
	}

	public User login(Connection connection, String email, String password) throws SQLException {

		String sql = "SELECT user_id, full_name, email, phone, password, role " + "FROM users "
				+ "WHERE email = ? AND password = ?";

		try (PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setString(1, email);
			ps.setString(2, password);

			try (ResultSet rs = ps.executeQuery()) {

				if (rs.next()) {

					User user = new User();

					user.setUserId(rs.getLong("user_id"));
					user.setFullName(rs.getString("full_name"));
					user.setEmail(rs.getString("email"));
					user.setPhone(rs.getString("phone"));
					user.setPassword(rs.getString("password"));
					user.setRole(rs.getString("role"));

					return user;
				}
			}
		}

		return null;
	}

}
