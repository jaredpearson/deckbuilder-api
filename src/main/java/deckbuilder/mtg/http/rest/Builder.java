package deckbuilder.mtg.http.rest;

import java.net.URI;

import javax.annotation.Nonnull;

import deckbuilder.mtg.Version;

/**
 * Builds the instance of the specified type
 * @author jared.pearson
 *
 * @param <T> the type the builder will build
 */
public interface Builder<T> {
	/**
	 * Builds the instance of the type
	 */
	public T build(BuildContext context);
	
	public static interface BuildContext {
		/**
		 * Gets the version to build with
		 */
		public @Nonnull Version getVersion();
		
		public @Nonnull URI getRequestUri();
	}
}
