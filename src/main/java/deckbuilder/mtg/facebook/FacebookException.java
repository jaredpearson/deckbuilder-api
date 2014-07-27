package deckbuilder.mtg.facebook;

/**
 * Exception thrown when the Facebook service returns an error 
 * @author jared.pearson
 */
public class FacebookException extends Exception {
	private static final long serialVersionUID = -4357249020315258521L;
	private final String rawResponse;
	private final String errorMessage;
	private final String errorType;
	private final int errorCode; 
	private final int errorSubcode;
	
	public FacebookException(String rawResponse) {
		this(rawResponse, "Error occurred while contacting Facebook", null, -1, -1);
	}
	
	public FacebookException(String rawResponse, String errorMessage, String errorType, int errorCode, int errorSubcode) {
		super("Error response received from Facebook: " + rawResponse);
		this.rawResponse = rawResponse;
		this.errorMessage = errorMessage;
		this.errorType = errorType;
		this.errorCode = errorCode;
		this.errorSubcode = errorSubcode;
	}

	/**
	 * Gets the response that was returned by Facebook
	 */
	public String getRawResponse() {
		return rawResponse;
	}
	
	/**
	 * Gets the error message that is returned by Facebook
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	public String getErrorType() {
		return errorType;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public int getErrorSubcode() {
		return errorSubcode;
	}
	
}
