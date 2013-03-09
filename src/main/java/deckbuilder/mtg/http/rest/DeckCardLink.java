package deckbuilder.mtg.http.rest;

import deckbuilder.mtg.entities.DeckCard;

public class DeckCardLink extends Link {
	private static final long serialVersionUID = 1L;

	public DeckCardLink(DeckCard deckCard) {
		this.put("id", deckCard.getId());
	}
}
