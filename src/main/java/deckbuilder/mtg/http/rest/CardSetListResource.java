package deckbuilder.mtg.http.rest;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * Resource for when a user requests a list of {@link CardSetResource} instances.
 * @author jared.pearson
 */
public class CardSetListResource {
	private final List<CardSetResource> cardSets;
	
	public CardSetListResource(@Nonnull List<CardSetResource> cardSets) {
		assert cardSets != null;
		this.cardSets = cardSets;
	}
	
	/**
	 * Gets the list of {@link CardSetResource} instances.
	 */
	public List<CardSetResource> getCardSets() {
		return cardSets;
	}
}
