package deckbuilder.mtg.http.rest;

import deckbuilder.mtg.entities.CardSet;

public class CardSetLink extends Link {
	private static final long serialVersionUID = -4771829553787714040L;
	
	public CardSetLink(CardSet cardSet) {
		this.putEntityUrl(CardSet.class, cardSet.getId());
		this.put("id", cardSet.getId());
	}
}
