package deckbuilder.mtg.http.rest;

import deckbuilder.mtg.entities.Deck;

public class DeckLink extends Link {
	private static final long serialVersionUID = 1L;
	
	public DeckLink(Deck deck) {
		put("id", deck.getId());
	}
}
