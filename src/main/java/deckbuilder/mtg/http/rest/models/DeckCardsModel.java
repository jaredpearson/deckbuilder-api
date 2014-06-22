package deckbuilder.mtg.http.rest.models;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * Resource used for displaying all of the cards associated to a deck 
 * @author jared.pearson
 */
public class DeckCardsModel implements Serializable {
	private static final long serialVersionUID = -7745626452731068674L;
	private final String url;
	private final List<DeckCardModel> cards;
	
	public DeckCardsModel(@Nonnull String url, @Nonnull List<DeckCardModel> cards) {
		assert url != null;
		assert cards != null;
		this.url = url;
		this.cards = cards;
	}
	
	/**
	 * Gets the URL of this
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * Gets the cards associated to the deck
	 */
	public List<DeckCardModel> getCards() {
		return cards;
	}
}
