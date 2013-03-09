package deckbuilder.mtg.http;

import deckbuilder.mtg.entities.User;

/**
 * Provider of authentication information.
 * @author jared.pearson
 */
public interface AuthenticationProvider {
	/**
	 * Given a token, the user should be authenticated. The token is dependent on
	 * the implementation.
	 * @param token The token recieved by the user.
	 * @return
	 * @throws AuthenticationException Thrown when the token is invalid or incorrect.
	 */
	public User authenticate(String token) throws AuthenticationException;
}