package app.service;

import java.util.List;

import app.entity.Customer;
import app.entity.Transaction;
import app.entity.User;
import app.exception.BankException;
import app.utils.InputUtil;

public class EmployeeService {

	private static BankService bankService = new BankService();

	// ---------- entry menu: Sign Up / Login / Back ----------

	public static void employeeMenu() {

		while (true) {

			System.out.println();
			System.out.println("==========================================");
			System.out.println("            EMPLOYEE BANKING");
			System.out.println("==========================================");

			System.out.println("1. Sign Up");
			System.out.println("2. Login");
			System.out.println("3. Back");

			int choice = InputUtil.readInt("Enter your choice: ");

			if (choice == 1) {
				employeeSignUpScreen();
			} else if (choice == 2) {
				employeeLoginScreen();
			} else if (choice == 3) {
				break;
			} else {
				System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	// ---------- sign up ----------

	public static void employeeSignUpScreen() {

		System.out.println();
		System.out.println("==============================================");
		System.out.println("              EMPLOYEE SIGN UP");
		System.out.println("==============================================");

		String fullName = InputUtil.readString("Enter full name: ");
		String email = InputUtil.readString("Enter email: ");
		String phone = InputUtil.readString("Enter phone number: ");
		String password = InputUtil.readString("Create password: ");
		String confirmPassword = InputUtil.readString("Confirm password: ");
		String staffCode = InputUtil.readString("Enter staff code: ");

		try {
			bankService.registerEmployee(fullName, email, phone, password, confirmPassword, staffCode);

			System.out.println();
			System.out.println("==============================================");
			System.out.println("      EMPLOYEE REGISTRATION SUCCESSFUL");
			System.out.println("==============================================");
			System.out.println("Welcome, " + fullName + "! You can now log in.");
			System.out.println("==============================================");

		} catch (BankException e) {
			System.out.println();
			System.out.println("Registration failed!");
			System.out.println("Reason: " + e.getMessage());
		}
	}

	// ---------- login ----------

	public static void employeeLoginScreen() {

		System.out.println();
		System.out.println("==============================================");
		System.out.println("              EMPLOYEE LOGIN");
		System.out.println("==============================================");

		String email = InputUtil.readString("Enter email: ");
		String password = InputUtil.readString("Enter password: ");

		try {
			User emp = bankService.login(email, password);

			if (!"EMPLOYEE".equals(emp.getRole())) {
				System.out.println("Access denied. Employees only.");
				return;
			}

			System.out.println();
			System.out.println("==============================================");
			System.out.println("             LOGIN SUCCESSFUL");
			System.out.println("==============================================");
			System.out.println("Welcome, " + emp.getFullName() + "!");
			System.out.println("==============================================");

			employeeDashboard(emp);

		} catch (BankException e) {
			System.out.println();
			System.out.println("Login failed!");
			System.out.println("Reason: " + e.getMessage());
		}
	}

	// ---------- dashboard ----------

	public static void employeeDashboard(User emp) {

		while (true) {
			System.out.println();
			System.out.println("==============================================");
			System.out.println("             EMPLOYEE DASHBOARD");
			System.out.println("==============================================");
			System.out.println("Welcome : " + emp.getFullName());
			System.out.println("----------------------------------------------");
			System.out.println("1. View All Customers");
			System.out.println("2. View Customer Transactions");
			System.out.println("3. Freeze Account");
			System.out.println("4. Activate Account");
			System.out.println("5. Logout");

			int choice = InputUtil.readInt("Enter choice: ");

			try {
				switch (choice) {
				case 1:
					List<Customer> list = bankService.viewAllCustomers();
					System.out.printf("%n%-20s %-28s %-12s %-12s %-10s%n", "NAME", "EMAIL", "ACCOUNT", "BALANCE",
							"STATUS");
					System.out.println(
							"-----------------------------------------------------------------------------------");
					for (Customer c : list) {
						System.out.printf("%-20s %-28s %-12s %-12.2f %-10s%n", c.getFullName(), c.getEmail(),
								c.getAccountNumber(), c.getBalance(), c.getAccountStatus());
					}
					break;
				case 2:
					String acc = InputUtil.readString("Enter account number: ");
					List<Transaction> txns = bankService.historyByAccountNumber(acc);
					CustomerService.printTransactions(txns);
					break;
				case 3:
					bankService.changeAccountStatus(InputUtil.readString("Account number to freeze: "), "FROZEN");
					System.out.println("Account frozen.");
					break;
				case 4:
					bankService.changeAccountStatus(InputUtil.readString("Account number to activate: "),
							"ACTIVE");
					System.out.println("Account activated.");
					break;
				case 5:
					System.out.println("You have been logged out successfully.");
					return;
				default:
					System.out.println("Invalid choice.");
				}
			} catch (BankException e) {
				System.out.println("Failed: " + e.getMessage());
			}
		}
	}
}