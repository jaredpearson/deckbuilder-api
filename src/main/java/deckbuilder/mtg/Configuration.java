package deckbuilder.mtg;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

public class Configuration {
	/**
	 * Returns the list of names that are always given administrator rights when created. This is useful when 
	 * setting up the app the first time.
	 */
	public final @Nonnull List<String> administratorNames;
	
	/**
	 * Returns the CORS configuration
	 */
	public final @Nonnull CorsConfiguration cors;

	public Configuration(@Nonnull List<String> administratorNames, @Nonnull CorsConfiguration cors) {
		assert administratorNames != null;
		assert cors != null;
		this.administratorNames = Collections.unmodifiableList(administratorNames);
		this.cors = cors;
	}
	
	/**
	 * Contains the configuration information for CORS 
	 * @author jared.pearson
	 */
	public static class CorsConfiguration {
		
		/**
		 * Gets the allowed origins for CORS
		 */
		public final @Nonnull String allowedOrigins;
		
		public CorsConfiguration(@Nonnull String allowedOrigins) {
			assert allowedOrigins != null;
			this.allowedOrigins = allowedOrigins;
		}
	}
}
