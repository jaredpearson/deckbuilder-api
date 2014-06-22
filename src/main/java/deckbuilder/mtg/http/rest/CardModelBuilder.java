package deckbuilder.mtg.http.rest;

import javax.annotation.Nonnull;

import deckbuilder.mtg.entities.Card;
import deckbuilder.mtg.entities.CardSet;

/**
 * Builder for card resource instances
 * @author jared.pearson
 */
class CardModelBuilder implements Builder<CardModel> {
	private final EntityUrlFactory urlFactory;
	private final Card card;
	
	/**
	 * Creates a new {@link CardModel} using the specified {@link Card} entity.
	 */
	public CardModelBuilder(@Nonnull EntityUrlFactory urlFactory, @Nonnull Card card) {
		assert urlFactory != null;
		assert card != null;
		this.urlFactory = urlFactory;
		this.card = card;
	}
	
	/**
	 * Builds an instance of the card resource
	 */
	@Override
	public @Nonnull CardModel build() {
		final String url = urlFactory.createEntityUrl(Card.class, card.getId());
		final String name = card.getName();
		final String castingCost = card.getCastingCost();
		final String powerToughness = card.getPowerToughness();
		final String typeLine = card.getTypeLine();
		final String rarity = card.getRarity();
		final String text = card.getText();
		final String setIndex = card.getSetIndex();
		final String author = card.getAuthor();
		final String cardSetUrl = urlFactory.createEntityUrl(CardSet.class, card.getSet().getId());
		final CardModel resource = new CardModel(url, name, castingCost, powerToughness, typeLine, rarity, text, setIndex, author, cardSetUrl);
		return resource;
	}
}