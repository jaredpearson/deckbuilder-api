package deckbuilder.mtg.http.rest.models;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Nonnull;

import deckbuilder.mtg.entities.CardSet;

/**
 * Resource used for when a user requests to view the cards associated to a {@link CardSet} 
 * @author jared.pearson
 */
public class CardSetCardsModel implements Serializable {
	private static final long serialVersionUID = -7708196997068077632L;
	
	private final List<CardModel> cards;
	
	public CardSetCardsModel(@Nonnull List<CardModel> cards) {
		assert cards != null;
		this.cards = cards;
	}
	
	/**
	 * Gets the cards
	 */
	public List<CardModel> getCards() {
		return cards;
	}
}