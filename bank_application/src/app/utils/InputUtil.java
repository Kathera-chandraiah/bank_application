package app.utils;

import java.util.Scanner;

public class InputUtil {
	
	static Scanner sc = new Scanner(System.in);
	
	public static String readString(String message) {
		System.out.println(message);
		String str = sc.nextLine();
		return str;
	}
	
	public static int readInt(String message) {
		System.out.println(message);
		int choice = Integer.parseInt(sc.nextLine());
		return choice;
	}

}
