package app;

import app.service.CustomerService;
import app.utils.InputUtil;

public class BankApplication {

	public static void main(String[] args) {

		System.out.println("==========================================");
		System.out.println("        WELCOME TO CHANDU BANK");
		System.out.println("==========================================");

		while (true) {

			System.out.println();
			System.out.println("1. Customer");
			System.out.println("2. Employee");
			System.out.println("3. Exit");

			int choice = InputUtil.readInt("Enter your choice: ");

			if (choice == 1) {

				CustomerService.customerMenu();

			} else if (choice == 2) {

			} else if (choice == 3) {

				System.out.println("Thank you for using Chandu Bank.");
				break;

			} else {

				System.out.println("Invalid choice. Please try again.");
			}
		}

	}

}
