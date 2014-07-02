package deckbuilder.mtg.http.rest;

import deckbuilder.mtg.entities.Card;
import deckbuilder.mtg.entities.CardSet;
import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.entities.DeckCard;
import deckbuilder.mtg.entities.User;

/**
 * Factory for creating URL builders
 * @author jared.pearson
 */
public class EntityUrlFactory {
	/**
	 * Creates a new URL builder for the specified entity with the given ID.
	 */
	public UrlBuilder createEntityUrl(Class<?> entity, Object id) {
		
		if(Card.class.isAssignableFrom(entity)) {
			return new CardIdResource.UrlBuilder((Long)id);
		} else if(Deck.class.isAssignableFrom(entity)) {
			return new DeckIdResource.UrlBuilder((Long)id);
		} else if(CardSet.class.isAssignableFrom(entity)) {
			return new CardSetIdResource.UrlBuilder((Long)id);
		} else if(User.class.isAssignableFrom(entity)) {
			return new UserIdResource.UrlBuilder((Long)id);
		} else if(DeckCard.class.isAssignableFrom(entity)) {
			return new DeckCardIdResource.UrlBuilder((Long)id);
		} else {
			throw new IllegalArgumentException("Unknown entity specified: " + entity.getClass().getName());
		}
		
	}
}
