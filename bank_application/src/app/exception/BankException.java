package app.exception;

public class BankException extends RuntimeException{
	private static final long serialVersionUID = -1650054218400907623L;

	public BankException(String message) {
		super(message);
	}

}
