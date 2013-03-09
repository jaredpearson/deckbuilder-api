package deckbuilder.mtg.service;

public class NoResultException extends RuntimeException {
	private static final long serialVersionUID = -2159310554053321311L;

	public NoResultException() {
		super();
	}

	public NoResultException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoResultException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoResultException(String message) {
		super(message);
	}

	public NoResultException(Throwable cause) {
		super(cause);
	}

}
