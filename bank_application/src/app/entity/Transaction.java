package app.entity;
import java.sql.Timestamp;
public class Transaction {
	private long txnId;
	private String accountNumber;
	private String txnType;
	private double amount;
	private double balanceAfter;
	private String otherAccount;
	private Timestamp createdAt;

	public Transaction() {
	}

	public Transaction(String accountNumber, String txnType, double amount, double balanceAfter,
			String otherAccount) {
		this.accountNumber = accountNumber;
		this.txnType = txnType;
		this.amount = amount;
		this.balanceAfter = balanceAfter;
		this.otherAccount = otherAccount;
	}

	public long getTxnId() { 
		return txnId; 
	}
	public void setTxnId(long txnId) { 
		this.txnId = txnId; 
		}

	public String getAccountNumber() { 
		return accountNumber; 
		}
	public void setAccountNumber(String accountNumber) { 
		this.accountNumber = accountNumber; 
		}

	public String getTxnType() { 
		return txnType; 
	}
	public void setTxnType(String txnType) { 
		this.txnType = txnType; 
	}

	public double getAmount() { 
		return amount; 
	}
	public void setAmount(double amount) { 
		this.amount = amount; 
		}

	public double getBalanceAfter() { 
		return balanceAfter; 
		}
	public void setBalanceAfter(double balanceAfter) { 
		this.balanceAfter = balanceAfter; 
	}

	public String getOtherAccount() { 
		return otherAccount; 
	}
	public void setOtherAccount(String otherAccount) { 
		this.otherAccount = otherAccount; 
	}

	public Timestamp getCreatedAt() { 
		return createdAt; 
	}
	public void setCreatedAt(Timestamp createdAt) { 
		this.createdAt = createdAt; 
	}
}
