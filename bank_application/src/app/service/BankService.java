package app.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import app.dao.AccountDao;
import app.dao.TransactionDao;
import app.dao.UserDao;
import app.entity.Account;
import app.entity.Customer;
import app.entity.Transaction;
import app.entity.User;
import app.exception.BankException;
import app.utils.DBConnection;

public class BankService {

	private UserDao userDao = new UserDao();
	private AccountDao accountDao = new AccountDao();
	private TransactionDao transactionDao = new TransactionDao();

	// Only real bank staff should know this. Change it to your own secret.
	private static final String EMPLOYEE_SECRET_CODE = "CHANDU@STAFF2026";

	// =============== CUSTOMER REGISTER ===============

	public String register(String fullName, String email, String phone, String password, String confirmPassword)
			throws BankException {

		validateSignup(fullName, email, phone, password, confirmPassword);

		Connection connection = null;

		try {
			connection = DBConnection.getConnection();
			connection.setAutoCommit(false);

			if (userDao.existsByEmail(connection, email) || userDao.existsByPhone(connection, phone)) {
				throw new BankException("Email or phone number already registered.");
			}

			User user = new User(fullName, email, phone, password, "CUSTOMER");
			long userId = userDao.createUser(connection, user);

			String accountNumber = generateAccountNumber();
			accountDao.createAccount(connection, userId, accountNumber);

			connection.commit();
			return accountNumber;

		} catch (BankException e) {
			rollbackQuietly(connection);
			throw e;
		} catch (SQLException e) {
			rollbackQuietly(connection);
			throw new BankException("Registration failed. Please try again.");
		} finally {
			closeQuietly(connection);
		}
	}

	// =============== EMPLOYEE REGISTER ===============

	public void registerEmployee(String fullName, String email, String phone, String password,
			String confirmPassword, String staffCode) throws BankException {

		if (staffCode == null || !EMPLOYEE_SECRET_CODE.equals(staffCode)) {
			throw new BankException("Invalid staff code. Employee registration denied.");
		}

		validateSignup(fullName, email, phone, password, confirmPassword);

		Connection connection = null;

		try {
			connection = DBConnection.getConnection();
			connection.setAutoCommit(false);

			if (userDao.existsByEmail(connection, email) || userDao.existsByPhone(connection, phone)) {
				throw new BankException("Email or phone number already registered.");
			}

			// employees get a user row only, no bank account
			User user = new User(fullName, email, phone, password, "EMPLOYEE");
			userDao.createUser(connection, user);

			connection.commit();

		} catch (BankException e) {
			rollbackQuietly(connection);
			throw e;
		} catch (SQLException e) {
			rollbackQuietly(connection);
			throw new BankException("Employee registration failed. Please try again.");
		} finally {
			closeQuietly(connection);
		}
	}

	private String generateAccountNumber() {
		long number = 1000000000L + (long) (Math.random() * 9000000000L);
		return String.valueOf(number);
	}

	private void validateSignup(String fullName, String email, String phone, String password,
			String confirmPassword) throws BankException {

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
		validatePassword(password);
		if (!password.equals(confirmPassword)) {
			throw new BankException("Password and confirm password do not match.");
		}
	}

	private void validatePassword(String password) {
		if (password == null
				|| !password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}$")) {
			throw new BankException("Password must contain at least 8 characters, "
					+ "one uppercase, one lowercase, one number, and one special character.");
		}
	}

	// =============== LOGIN ===============

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

	// =============== HELPERS ===============

	private void rollbackQuietly(Connection connection) {
		if (connection != null) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	private void closeQuietly(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void validateAmount(double amount) {
		if (amount <= 0) {
			throw new BankException("Amount must be greater than zero.");
		}
		if (amount > 100000) {
			throw new BankException("Maximum allowed per transaction is ₹1,00,000.");
		}
	}

	private void requireActive(Account account) {
		if (account == null) {
			throw new BankException("Account not found.");
		}
		if (!"ACTIVE".equals(account.getStatus())) {
			throw new BankException("Account is " + account.getStatus() + ". Operation not allowed.");
		}
	}

	// =============== BALANCE / DETAILS ===============

	public Account accountDetails(long userId) throws BankException {
		try (Connection connection = DBConnection.getConnection()) {
			Account account = accountDao.findByUserId(connection, userId);
			if (account == null) {
				throw new BankException("Account not found.");
			}
			return account;
		} catch (SQLException e) {
			throw new BankException("Unable to fetch account details.");
		}
	}

	public double checkBalance(long userId) throws BankException {
		return accountDetails(userId).getBalance();
	}

	// =============== DEPOSIT ===============

	public double depositMoney(long userId, double amount) throws BankException {
		validateAmount(amount);
		Connection connection = null;
		try {
			connection = DBConnection.getConnection();
			connection.setAutoCommit(false);

			Account acc = accountDao.findByUserId(connection, userId);
			if (acc == null) {
				throw new BankException("Account not found.");
			}

			acc = accountDao.findByAccountNumberForUpdate(connection, acc.getAccountNumber());
			requireActive(acc);

			double newBalance = acc.getBalance() + amount;
			accountDao.updateBalance(connection, acc.getAccountNumber(), newBalance);
			transactionDao.insert(connection,
					new Transaction(acc.getAccountNumber(), "DEPOSIT", amount, newBalance, null));

			connection.commit();
			return newBalance;

		} catch (BankException e) {
			rollbackQuietly(connection);
			throw e;
		} catch (SQLException e) {
			rollbackQuietly(connection);
			throw new BankException("Deposit failed. Please try again.");
		} finally {
			closeQuietly(connection);
		}
	}

	// =============== WITHDRAW ===============

	public double withdrawMoney(long userId, double amount) throws BankException {
		validateAmount(amount);
		Connection connection = null;
		try {
			connection = DBConnection.getConnection();
			connection.setAutoCommit(false);

			Account acc = accountDao.findByUserId(connection, userId);
			if (acc == null) {
				throw new BankException("Account not found.");
			}

			acc = accountDao.findByAccountNumberForUpdate(connection, acc.getAccountNumber());
			requireActive(acc);

			if (acc.getBalance() < amount) {
				throw new BankException("Insufficient balance.");
			}

			double newBalance = acc.getBalance() - amount;
			accountDao.updateBalance(connection, acc.getAccountNumber(), newBalance);
			transactionDao.insert(connection,
					new Transaction(acc.getAccountNumber(), "WITHDRAW", amount, newBalance, null));

			connection.commit();
			return newBalance;

		} catch (BankException e) {
			rollbackQuietly(connection);
			throw e;
		} catch (SQLException e) {
			rollbackQuietly(connection);
			throw new BankException("Withdrawal failed. Please try again.");
		} finally {
			closeQuietly(connection);
		}
	}

	// =============== TRANSFER ===============

	public double transferMoney(long userId, String toAccountNumber, double amount) throws BankException {
		validateAmount(amount);
		if (toAccountNumber == null || toAccountNumber.trim().isEmpty()) {
			throw new BankException("Receiver account number cannot be empty.");
		}

		Connection connection = null;
		try {
			connection = DBConnection.getConnection();
			connection.setAutoCommit(false);

			Account mine = accountDao.findByUserId(connection, userId);
			if (mine == null) {
				throw new BankException("Account not found.");
			}
			if (mine.getAccountNumber().equals(toAccountNumber)) {
				throw new BankException("You cannot transfer to your own account.");
			}

			// Lock the smaller account number first so two opposite transfers cannot deadlock
			String first = mine.getAccountNumber().compareTo(toAccountNumber) < 0 ? mine.getAccountNumber()
					: toAccountNumber;
			String second = first.equals(mine.getAccountNumber()) ? toAccountNumber : mine.getAccountNumber();

			Account a1 = accountDao.findByAccountNumberForUpdate(connection, first);
			Account a2 = accountDao.findByAccountNumberForUpdate(connection, second);

			Account from = first.equals(mine.getAccountNumber()) ? a1 : a2;
			Account to = first.equals(mine.getAccountNumber()) ? a2 : a1;

			if (to == null) {
				throw new BankException("Receiver account does not exist.");
			}
			requireActive(from);
			if (!"ACTIVE".equals(to.getStatus())) {
				throw new BankException("Receiver account is not active.");
			}
			if (from.getBalance() < amount) {
				throw new BankException("Insufficient balance.");
			}

			double fromBalance = from.getBalance() - amount;
			double toBalance = to.getBalance() + amount;

			accountDao.updateBalance(connection, from.getAccountNumber(), fromBalance);
			accountDao.updateBalance(connection, to.getAccountNumber(), toBalance);

			transactionDao.insert(connection, new Transaction(from.getAccountNumber(), "TRANSFER_OUT", amount,
					fromBalance, to.getAccountNumber()));
			transactionDao.insert(connection, new Transaction(to.getAccountNumber(), "TRANSFER_IN", amount,
					toBalance, from.getAccountNumber()));

			connection.commit();
			return fromBalance;

		} catch (BankException e) {
			rollbackQuietly(connection);
			throw e;
		} catch (SQLException e) {
			rollbackQuietly(connection);
			throw new BankException("Transfer failed. Please try again.");
		} finally {
			closeQuietly(connection);
		}
	}

	// =============== TRANSACTION HISTORY ===============

	public List<Transaction> transactionHistory(long userId) throws BankException {
		try (Connection connection = DBConnection.getConnection()) {
			Account acc = accountDao.findByUserId(connection, userId);
			if (acc == null) {
				throw new BankException("Account not found.");
			}
			return transactionDao.findByAccount(connection, acc.getAccountNumber(), 10);
		} catch (SQLException e) {
			throw new BankException("Unable to fetch transaction history.");
		}
	}

	// =============== CHANGE PASSWORD ===============

	public void changePassword(User user, String oldPassword, String newPassword, String confirmPassword)
			throws BankException {

		if (oldPassword == null || !oldPassword.equals(user.getPassword())) {
			throw new BankException("Old password is incorrect.");
		}
		validatePassword(newPassword);
		if (!newPassword.equals(confirmPassword)) {
			throw new BankException("New password and confirm password do not match.");
		}
		if (newPassword.equals(oldPassword)) {
			throw new BankException("New password must be different from the old password.");
		}

		try (Connection connection = DBConnection.getConnection()) {
			userDao.updatePassword(connection, user.getUserId(), newPassword);
			user.setPassword(newPassword);
		} catch (SQLException e) {
			throw new BankException("Unable to change password. Please try again.");
		}
	}

	// =============== EMPLOYEE FEATURES ===============

	public List<Customer> viewAllCustomers() throws BankException {
		try (Connection connection = DBConnection.getConnection()) {
			return accountDao.findAllCustomers(connection);
		} catch (SQLException e) {
			throw new BankException("Unable to fetch customers.");
		}
	}

	public void changeAccountStatus(String accountNumber, String status) throws BankException {
		try (Connection connection = DBConnection.getConnection()) {
			if (accountDao.updateStatus(connection, accountNumber, status) == 0) {
				throw new BankException("Account not found.");
			}
		} catch (SQLException e) {
			throw new BankException("Unable to update account status.");
		}
	}

	public List<Transaction> historyByAccountNumber(String accountNumber) throws BankException {
		try (Connection connection = DBConnection.getConnection()) {
			return transactionDao.findByAccount(connection, accountNumber, 20);
		} catch (SQLException e) {
			throw new BankException("Unable to fetch transactions.");
		}
	}
}