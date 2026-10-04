package app.entity;

public class Customer extends User {

	private String accountNumber;
	private Double balance;
	private String accountStatus;

	public Customer() {
	}

	public Customer(String fullName, String email, String phone, String password, String role, String accountNumber,
			Double balance, String accountStatus) {

		super(fullName, email, phone, password, role);

		this.accountNumber = accountNumber;
		this.balance = balance;
		this.accountStatus = accountStatus;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public String getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(String accountStatus) {
		this.accountStatus = accountStatus;
	}

}
