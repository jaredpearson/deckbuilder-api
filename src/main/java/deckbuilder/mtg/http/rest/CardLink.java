package deckbuilder.mtg.http.rest;

import java.util.ArrayList;
import java.util.List;

import deckbuilder.mtg.entities.Card;

public class CardLink extends Link {
	private static final long serialVersionUID = 1L;
	
	public CardLink(Card card) {
		this.putEntityUrl(Card.class, card.getId());
		this.put("id", card.getId());
	}
	
	public static List<CardLink> create(List<Card> cards) {
		ArrayList<CardLink> links = new ArrayList<>(cards.size());
		for(Card card : cards) {
			links.add(new CardLink(card));
		}
		return links;
	}
}
