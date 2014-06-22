package deckbuilder.mtg;

import javax.annotation.Nonnull;

/**
 * Represents one version of the application.
 * @author jared.pearson
 */
public class Version {
	private final String apiValue;
	
	public Version(@Nonnull String apiValue) {
		assert apiValue != null;
		this.apiValue = apiValue;
	}
	
	/**
	 * Gets the value that is to be displayed in the API.
	 */
	public String getApiValue() {
		return apiValue;
	}
}
