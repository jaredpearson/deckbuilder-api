package deckbuilder.mtg.http.rest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Response for a save operation
 * @author jared.pearson
 */
public class SaveResponse {
	private final String[] messages;
	private final Boolean success;
	
	public SaveResponse(Boolean success, String[] messages) {
		this.success = success;
		this.messages = messages;
	}
	
	/**
	 * Gets any messages that were caused by the save operation or null if none
	 * were added. If the success was false, there should be at least one message.
	 */
	public @Nullable String[] getMessages() {
		return messages;
	}
	
	/**
	 * Gets the success status of the save
	 */
	public Boolean getSuccess() {
		return success;
	}
	
	/**
	 * Creates a new save response that represents a fail.
	 */
	public static @Nonnull SaveResponse fail(@Nonnull String... messages) {
		assert messages != null;
		assert messages.length > 0;
		return new SaveResponse(false, messages);
	}
	
	/**
	 * Creates a new save response that represents a success.
	 */
	public static @Nonnull SaveResponse success() {
		return new SaveResponse(true, null);
	}
}