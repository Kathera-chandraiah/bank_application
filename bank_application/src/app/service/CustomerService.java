package app.service;

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

	    String fullName =
	            InputUtil.readString("Enter full name: ");

	    String email =
	            InputUtil.readString("Enter email: ");

	    String phone =
	            InputUtil.readString("Enter phone number: ");

	    String password =
	            InputUtil.readString("Create password: ");

	    String confirmPassword =
	            InputUtil.readString("Confirm password: ");

	    try {
	    	
	        String accountNumber =
	                bankService.register(
	                        fullName,
	                        email,
	                        phone,
	                        password,
	                        confirmPassword
	                );

	        System.out.println();
	        System.out.println("==============================================");
	        System.out.println("         REGISTRATION SUCCESSFUL");
	        System.out.println("==============================================");

	        System.out.println(
	                "Welcome, " + fullName + "!"
	        );

	        System.out.println(
	                "Your Account Number : "
	                        + accountNumber
	        );

	        System.out.println(
	                "Opening Balance     : ₹0.00"
	        );

	        System.out.println(
	                "Account Status      : ACTIVE"
	        );

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

	    String email =
	            InputUtil.readString("Enter email: ");

	    String password =
	            InputUtil.readString("Enter password: ");

	    try {

	        User user =
	                bankService.login(
	                        email,
	                        password
	                );

	        System.out.println();
	        System.out.println("==============================================");
	        System.out.println("             LOGIN SUCCESSFUL");
	        System.out.println("==============================================");

	        System.out.println(
	                "Welcome, " + user.getFullName() + "!"
	        );

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

	        int choice =
	                InputUtil.readInt("Enter choice: ");

	        switch (choice) {

	        case 1:
	            System.out.println("Check Balance");
	            break;

	        case 2:
	            System.out.println("Deposit Money");
	            break;

	        case 3:
	            System.out.println("Withdraw Money");
	            break;

	        case 4:
	            System.out.println("Transfer Money");
	            break;

	        case 5:
	            System.out.println("Transaction History");
	            break;

	        case 6:
	            System.out.println("Account Details");
	            break;

	        case 7:
	            System.out.println("Change Password");
	            break;

	        case 8:
	            System.out.println(
	                    "You have been logged out successfully."
	            );
	            return;

	        default:
	            System.out.println(
	                    "Invalid choice."
	            );
	        }
	    }
	}
	
	
	
	
	

}
