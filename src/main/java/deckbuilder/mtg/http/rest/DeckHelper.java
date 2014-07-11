package deckbuilder.mtg.http.rest;

import javax.annotation.Nonnull;
import javax.ws.rs.core.SecurityContext;

import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.http.Principal;

/**
 * Helper methods for dealing with Decks
 * @author jared.pearson
 */
final class DeckHelper {
	
	private DeckHelper() {
	}
	
	/**
	 * Checks the current user to see if they are the deck's owner (or an administrator). If not, then a {@link NotAuthorizedException} is thrown.
	 */
	public static void assertOwnerOrAdmin(@Nonnull Deck deck, @Nonnull SecurityContext securityContext) {
		assert deck != null;
		assert securityContext != null;
		
		final Principal principal = (Principal)securityContext.getUserPrincipal();
		if(!securityContext.isUserInRole("administrator") && !deck.getOwner().equals(principal.getUser())) {
			throw new NotAuthorizedException("Invalid owner. Only the owner can update the deck.", SaveResponse.fail("Invalid owner. Only the owner can update the deck."));
		}
	}
	
}
