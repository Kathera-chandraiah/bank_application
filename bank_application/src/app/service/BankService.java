package app.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import app.dao.AccountDao;
import app.dao.UserDao;
import app.entity.User;
import app.exception.BankException;
import app.utils.DBConnection;

public class BankService {

	private UserDao userDao = new UserDao();
	private AccountDao accountDao = new AccountDao();

	public String register(String fullName, String email, String phone, String password, String confirmPassword)
			throws BankException {

		// 1. Validate input
		validateSignup(fullName, email, phone, password, confirmPassword);

		Connection connection = null;

		try {

			connection = DBConnection.getConnection();

			// 2. Start transaction
			connection.setAutoCommit(false);

			// 3. Check duplicate email/phone
			if (userDao.existsByEmail(connection, email)) {

				throw new BankException("Email or phone number already registered.");
			}

			// 4. Create User object
			User user = new User(fullName, email, phone, password, "CUSTOMER");

			// 5. Insert user
			long userId = userDao.createUser(connection, user);

			// 6. Generate account number
			String accountNumber = generateAccountNumber();

			// 7. Create bank account
			accountDao.createAccount(connection, userId, accountNumber);

			// 8. Everything successful
			connection.commit();

			return accountNumber;

		} catch (SQLException e) {

			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}

			throw new BankException("Registration failed. Please try again.");

		} finally {

			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private String generateAccountNumber() {

		long number = 1000000000L + (long) (Math.random() * 9000000000L);

		return String.valueOf(number);
	}

	private void validateSignup(String fullName, String email, String phone, String password, String confirmPassword)
			throws BankException {

		if (fullName == null || fullName.trim().isEmpty()) {

			throw new BankException("Name cannot be empty.");
		}

		if (email == null || email.trim().isEmpty()) {

			throw new BankException("Email cannot be empty.");
		}

		if (!email.matches("^[A-Za-z0-9+_.-]+@gmail[.]com$")) {

			throw new BankException("Invalid email format.");
		}

		if (phone == null || !phone.matches("^[6-9][0-9]{9}$")) {

			throw new BankException("Mobile number must contain 10 digits and start with 6-9.");
		}

		if (password == null ||
			    !password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}$")) {

			    throw new BankException(
			        "Password must contain at least 8 characters, "
			        + "one uppercase, one lowercase, one number, "
			        + "and one special character."
			    );
			}

		if (!password.equals(confirmPassword)) {

			throw new BankException("Password and confirm password do not match.");
		}
	}

	public User login(String email, String password) throws BankException {

		if (email == null || email.trim().isEmpty()) {
			throw new BankException("Email cannot be empty.");
		}

		if (password == null || password.trim().isEmpty()) {
			throw new BankException("Password cannot be empty.");
		}

		try (Connection connection = DBConnection.getConnection()) {

			User user = userDao.login(connection, email, password);

			if (user == null) {
				throw new BankException("Invalid email or password.");
			}

			return user;

		} catch (SQLException e) {

			throw new BankException("Unable to login. Please try again.");
		}
	}

	public static void checkBalance() {
	}

	static void depositMoney() {
	}

	public static void withdrawMoney() {
	}

	public static void transferMoney() {
	}

	public static void transactionHistory() {
	}

	public static void accountDetails() {
	}

	public static void changePassword() {
	}
}
