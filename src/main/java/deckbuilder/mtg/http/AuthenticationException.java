package deckbuilder.mtg.http;

/**
 * Thrown by {@link AuthenticationProvider} when an exception occurs during 
 * authentication. 
 * @author jared.pearson
 */
public class AuthenticationException extends Exception {
	private static final long serialVersionUID = 1L;

	public AuthenticationException() {
	}

	public AuthenticationException(String message) {
		super(message);
	}

	public AuthenticationException(Throwable cause) {
		super(cause);
	}

	public AuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}

	public AuthenticationException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
