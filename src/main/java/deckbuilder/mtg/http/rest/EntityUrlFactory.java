package deckbuilder.mtg.http.rest;

import deckbuilder.mtg.entities.Card;
import deckbuilder.mtg.entities.CardSet;
import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.entities.User;

public class EntityUrlFactory {
	public UrlBuilder createEntityUrl(Class<?> entity, Object id) {
		
		if(Card.class.isAssignableFrom(entity)) {
			return new CardIdResource.UrlBuilder((Long)id);
		} else if(Deck.class.isAssignableFrom(entity)) {
			return new DeckIdResource.UrlBuilder((Long)id);
		} else if(CardSet.class.isAssignableFrom(entity)) {
			return new CardSetIdResource.UrlBuilder((Long)id);
		} else if(User.class.isAssignableFrom(entity)) {
			return new UserIdResource.UrlBuilder((Long)id);
		} else {
			throw new IllegalArgumentException("Unknown entity specified: " + entity.getClass().getName());
		}
		
	}
}
