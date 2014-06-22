package deckbuilder.mtg.http.rest;

import java.net.URI;

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
		public URI getRequestUri();
	}
}
