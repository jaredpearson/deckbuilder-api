package deckbuilder.mtg.http.rest.models;

import javax.annotation.Nonnull;

import deckbuilder.mtg.entities.Card;
import deckbuilder.mtg.entities.CardSet;
import deckbuilder.mtg.http.rest.Builder;
import deckbuilder.mtg.http.rest.EntityUrlFactory;
import deckbuilder.mtg.http.rest.UrlBuilder;

/**
 * Builder for card resource instances
 * @author jared.pearson
 */
public class CardModelBuilder implements Builder<CardModel> {
	private final UrlBuilder url;
	private final long id;
	private final String name;
	private final String castingCost;
	private final String powerToughness;
	private final String typeLine;
	private final String rarity;
	private final String text;
	private final String setIndex;
	private final String artist;
	private final UrlBuilder cardSetUrl;
	
	/**
	 * Creates a new {@link CardModel} using the specified {@link Card} entity.
	 */
	public CardModelBuilder(@Nonnull EntityUrlFactory urlFactory, @Nonnull Card card) {
		assert urlFactory != null;
		assert card != null;

		this.url = urlFactory.createEntityUrl(Card.class, card.getId());
		this.id = card.getId();
		this.name = card.getName();
		this.castingCost = card.getCastingCost();
		this.powerToughness = card.getPowerToughness();
		this.typeLine = card.getTypeLine();
		this.rarity = card.getRarity();
		this.text = card.getText();
		this.setIndex = card.getSetIndex();
		this.artist = card.getArtist();
		this.cardSetUrl = urlFactory.createEntityUrl(CardSet.class, card.getSet().getId());
	}
	
	/**
	 * Builds an instance of the card resource
	 */
	@Override
	public @Nonnull CardModel build(BuildContext context) {
		final String url = this.url.build(context);
		final String cardSetUrl = this.cardSetUrl.build(context);
		return new CardModel(
				id, 
				url, 
				name, 
				castingCost, 
				powerToughness, 
				typeLine, 
				rarity, 
				text, 
				setIndex, 
				artist, 
				cardSetUrl);
	}
}