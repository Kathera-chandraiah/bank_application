package app.service;

import java.util.List;

import app.entity.Account;
import app.entity.Transaction;
import app.entity.User;
import app.exception.BankException;
import app.utils.InputUtil;

public class CustomerService {

	private static BankService bankService = new BankService();

	public static void customerMenu() {

		while (true) {

			System.out.println();
			System.out.println("==========================================");
			System.out.println("           CUSTOMER BANKING");
			System.out.println("==========================================");

			System.out.println("1. Sign Up");
			System.out.println("2. Login");
			System.out.println("3. Back");

			int choice = InputUtil.readInt("Enter your choice: ");

			if (choice == 1) {
				signUpScreen();
			} else if (choice == 2) {
				loginScreen();
			} else if (choice == 3) {
				break;
			} else {
				System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	public static void signUpScreen() {

		System.out.println();
		System.out.println("==============================================");
		System.out.println("              CUSTOMER SIGN UP");
		System.out.println("==============================================");

		String fullName = InputUtil.readString("Enter full name: ");
		String email = InputUtil.readString("Enter email: ");
		String phone = InputUtil.readString("Enter phone number: ");
		String password = InputUtil.readString("Create password: ");
		String confirmPassword = InputUtil.readString("Confirm password: ");

		try {
			String accountNumber = bankService.register(fullName, email, phone, password, confirmPassword);

			System.out.println();
			System.out.println("==============================================");
			System.out.println("         REGISTRATION SUCCESSFUL");
			System.out.println("==============================================");
			System.out.println("Welcome, " + fullName + "!");
			System.out.println("Your Account Number : " + accountNumber);
			System.out.println("Opening Balance     : ₹0.00");
			System.out.println("Account Status      : ACTIVE");
			System.out.println("==============================================");

		} catch (BankException e) {
			System.out.println();
			System.out.println("Registration failed!");
			System.out.println("Reason: " + e.getMessage());
		}
	}

	public static void loginScreen() {

		System.out.println();
		System.out.println("==============================================");
		System.out.println("              CUSTOMER LOGIN");
		System.out.println("==============================================");

		String email = InputUtil.readString("Enter email: ");
		String password = InputUtil.readString("Enter password: ");

		try {
			User user = bankService.login(email, password);

			if (!"CUSTOMER".equals(user.getRole())) {
				System.out.println("This login is for customers only.");
				return;
			}

			System.out.println();
			System.out.println("==============================================");
			System.out.println("             LOGIN SUCCESSFUL");
			System.out.println("==============================================");
			System.out.println("Welcome, " + user.getFullName() + "!");
			System.out.println("==============================================");

			customerDashboard(user);

		} catch (BankException e) {
			System.out.println();
			System.out.println("Login failed!");
			System.out.println("Reason: " + e.getMessage());
		}
	}

	public static void customerDashboard(User user) {

		while (true) {

			System.out.println();
			System.out.println("==============================================");
			System.out.println("             CUSTOMER DASHBOARD");
			System.out.println("==============================================");
			System.out.println("Welcome : " + user.getFullName());
			System.out.println("Email   : " + user.getEmail());
			System.out.println("----------------------------------------------");
			System.out.println("1. Check Balance");
			System.out.println("2. Deposit Money");
			System.out.println("3. Withdraw Money");
			System.out.println("4. Transfer Money");
			System.out.println("5. Transaction History");
			System.out.println("6. Account Details");
			System.out.println("7. Change Password");
			System.out.println("8. Logout");
			System.out.println("==============================================");

			int choice = InputUtil.readInt("Enter choice: ");

			switch (choice) {
			case 1:
				checkBalanceScreen(user);
				break;
			case 2:
				depositScreen(user);
				break;
			case 3:
				withdrawScreen(user);
				break;
			case 4:
				transferScreen(user);
				break;
			case 5:
				historyScreen(user);
				break;
			case 6:
				accountDetailsScreen(user);
				break;
			case 7:
				changePasswordScreen(user);
				break;
			case 8:
				System.out.println("You have been logged out successfully.");
				return;
			default:
				System.out.println("Invalid choice.");
			}
		}
	}

	public static void checkBalanceScreen(User user) {
		try {
			double balance = bankService.checkBalance(user.getUserId());
			System.out.printf("%nCurrent Balance: ₹%.2f%n", balance);
		} catch (BankException e) {
			System.out.println("Failed: " + e.getMessage());
		}
	}

	public static void depositScreen(User user) {
		double amount = InputUtil.readDouble("Enter amount to deposit: ");
		try {
			double newBalance = bankService.depositMoney(user.getUserId(), amount);
			System.out.printf("%nDeposit successful! New balance: ₹%.2f%n", newBalance);
		} catch (BankException e) {
			System.out.println("Deposit failed: " + e.getMessage());
		}
	}

	public static void withdrawScreen(User user) {
		double amount = InputUtil.readDouble("Enter amount to withdraw: ");
		try {
			double newBalance = bankService.withdrawMoney(user.getUserId(), amount);
			System.out.printf("%nWithdrawal successful! New balance: ₹%.2f%n", newBalance);
		} catch (BankException e) {
			System.out.println("Withdrawal failed: " + e.getMessage());
		}
	}

	public static void transferScreen(User user) {
		String toAccount = InputUtil.readString("Enter receiver account number: ");
		double amount = InputUtil.readDouble("Enter amount to transfer: ");
		try {
			double newBalance = bankService.transferMoney(user.getUserId(), toAccount, amount);
			System.out.printf("%nTransfer successful! New balance: ₹%.2f%n", newBalance);
		} catch (BankException e) {
			System.out.println("Transfer failed: " + e.getMessage());
		}
	}

	public static void historyScreen(User user) {
		try {
			List<Transaction> list = bankService.transactionHistory(user.getUserId());
			printTransactions(list);
		} catch (BankException e) {
			System.out.println("Failed: " + e.getMessage());
		}
	}

	static void printTransactions(List<Transaction> list) {
		if (list.isEmpty()) {
			System.out.println("No transactions found.");
			return;
		}
		System.out.println();
		System.out.printf("%-6s %-14s %-12s %-12s %-12s %s%n", "ID", "TYPE", "AMOUNT", "BALANCE", "OTHER A/C",
				"DATE");
		System.out.println("-------------------------------------------------------------------------------");
		for (Transaction t : list) {
			System.out.printf("%-6d %-14s %-12.2f %-12.2f %-12s %s%n", t.getTxnId(), t.getTxnType(),
					t.getAmount(), t.getBalanceAfter(), t.getOtherAccount() == null ? "-" : t.getOtherAccount(),
					t.getCreatedAt());
		}
	}

	public static void accountDetailsScreen(User user) {
		try {
			Account acc = bankService.accountDetails(user.getUserId());
			System.out.println();
			System.out.println("==============================================");
			System.out.println("               ACCOUNT DETAILS");
			System.out.println("==============================================");
			System.out.println("Name           : " + user.getFullName());
			System.out.println("Email          : " + user.getEmail());
			System.out.println("Phone          : " + user.getPhone());
			System.out.println("Account Number : " + acc.getAccountNumber());
			System.out.printf("Balance        : ₹%.2f%n", acc.getBalance());
			System.out.println("Status         : " + acc.getStatus());
			System.out.println("==============================================");
		} catch (BankException e) {
			System.out.println("Failed: " + e.getMessage());
		}
	}

	public static void changePasswordScreen(User user) {
		String oldPwd = InputUtil.readString("Enter old password: ");
		String newPwd = InputUtil.readString("Enter new password: ");
		String confirm = InputUtil.readString("Confirm new password: ");
		try {
			bankService.changePassword(user, oldPwd, newPwd, confirm);
			System.out.println("Password changed successfully.");
		} catch (BankException e) {
			System.out.println("Password change failed: " + e.getMessage());
		}
	}
}