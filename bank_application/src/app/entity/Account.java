package app.entity;

public class Account {
	private long accountId;
	private long userId;
	private String accountNumber;
	private double balance;
	private String status;

	public long getAccountId() { 
		return accountId; 
	}
	public void setAccountId(long accountId) { 
		this.accountId = accountId; 
	}

	public long getUserId() { 
		return userId; 
	}
	public void setUserId(long userId) { 
		this.userId = userId;
	}

	public String getAccountNumber() { 
		return accountNumber; 
	}
	public void setAccountNumber(String accountNumber) { 
		this.accountNumber = accountNumber; 
	}

	public double getBalance() { 
		return balance; 
	}
	public void setBalance(double balance) { 
		this.balance = balance; 
	}

	public String getStatus() { 
		return status; 
	}
	public void setStatus(String status) { 
		this.status = status; 
	}


}
