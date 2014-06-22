package deckbuilder.mtg.http.rest;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Nonnull;

import deckbuilder.mtg.entities.CardSet;

/**
 * Resource used for when a user requests to view the cards associated to a {@link CardSet} 
 * @author jared.pearson
 */
public class CardSetCardsResource implements Serializable {
	private static final long serialVersionUID = -7708196997068077632L;
	
	private final List<CardResource> cards;
	
	public CardSetCardsResource(@Nonnull List<CardResource> cards) {
		assert cards != null;
		this.cards = cards;
	}
	
	/**
	 * Gets the cards
	 */
	public List<CardResource> getCards() {
		return cards;
	}
}