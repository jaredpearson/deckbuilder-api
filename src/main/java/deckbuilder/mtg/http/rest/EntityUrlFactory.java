package deckbuilder.mtg.http.rest;

import deckbuilder.mtg.entities.Card;
import deckbuilder.mtg.entities.CardSet;
import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.entities.User;

public class EntityUrlFactory {
	private static final EntityUrlFactory INSTANCE = new EntityUrlFactory();
	
	//TODO: move this to some configuration file
	private static final String LATEST_VERSION = "v1";
	
	public String createEntityUrl(Class<?> entity, Object id) {
		
		if(Card.class.isAssignableFrom(entity)) {
			return "/" + LATEST_VERSION + "/card/" + id.toString();
		} else if(Deck.class.isAssignableFrom(entity)) {
			return "/" + LATEST_VERSION + "/deck/" + id.toString();
		} else if(CardSet.class.isAssignableFrom(entity)) {
			return "/" + LATEST_VERSION + "/set/" + id.toString();
		} else if(User.class.isAssignableFrom(entity)) {
			return "/" + LATEST_VERSION + "/user/" + id.toString();
		} else {
			throw new IllegalArgumentException("Unknown entity specified: " + entity.getClass().getName());
		}
		
	}
	
	public static EntityUrlFactory getInstance() {
		return INSTANCE;
	}
}
