package deckbuilder.mtg.http.rest;

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
	public T build();
}
