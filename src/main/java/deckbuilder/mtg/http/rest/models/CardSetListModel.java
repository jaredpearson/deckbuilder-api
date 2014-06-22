package deckbuilder.mtg.http.rest.models;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * Resource for when a user requests a list of {@link CardSetModel} instances.
 * @author jared.pearson
 */
public class CardSetListModel {
	private final List<CardSetModel> cardSets;
	
	public CardSetListModel(@Nonnull List<CardSetModel> cardSets) {
		assert cardSets != null;
		this.cardSets = cardSets;
	}
	
	/**
	 * Gets the list of {@link CardSetModel} instances.
	 */
	public List<CardSetModel> getCardSets() {
		return cardSets;
	}
}
