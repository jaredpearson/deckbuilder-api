package deckbuilder.mtg.http.rest;

import javax.annotation.Nonnull;

import deckbuilder.mtg.entities.CardSet;

/**
 * Builder for instances of {@link CardSetResource}
 * @author jared.pearson
 */
public class CardSetResourceBuilder implements Builder<CardSetResource> {
	private final EntityUrlFactory urlFactory;
	private final CardSet cardSet;
	
	public CardSetResourceBuilder(@Nonnull EntityUrlFactory urlFactory, @Nonnull CardSet cardSet) {
		assert urlFactory != null;
		assert cardSet != null;
		this.urlFactory = urlFactory;
		this.cardSet = cardSet;
	}
	
	/**
	 * Creates a new {@link CardSetResource} instance.
	 */
	@Override
	public CardSetResource build() {
		final String url = urlFactory.createEntityUrl(CardSet.class, cardSet.getId());
		final String name = cardSet.getName();
		final String abbreviation = cardSet.getAbbreviation();
		final String language = cardSet.getLanguage();
		final String cardsUrl = urlFactory.createEntityUrl(CardSet.class, cardSet.getId()) + "/cards";
		return new CardSetResource(url, name, abbreviation, language, cardsUrl);
	}
}