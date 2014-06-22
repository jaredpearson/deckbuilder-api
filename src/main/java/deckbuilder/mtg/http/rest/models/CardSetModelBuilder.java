package deckbuilder.mtg.http.rest.models;

import javax.annotation.Nonnull;

import deckbuilder.mtg.entities.CardSet;
import deckbuilder.mtg.http.rest.Builder;
import deckbuilder.mtg.http.rest.EntityUrlFactory;

/**
 * Builder for instances of {@link CardSetModel}
 * @author jared.pearson
 */
public class CardSetModelBuilder implements Builder<CardSetModel> {
	private final EntityUrlFactory urlFactory;
	private final CardSet cardSet;
	
	public CardSetModelBuilder(@Nonnull EntityUrlFactory urlFactory, @Nonnull CardSet cardSet) {
		assert urlFactory != null;
		assert cardSet != null;
		this.urlFactory = urlFactory;
		this.cardSet = cardSet;
	}
	
	/**
	 * Creates a new {@link CardSetModel} instance.
	 */
	@Override
	public CardSetModel build() {
		final String url = urlFactory.createEntityUrl(CardSet.class, cardSet.getId());
		final String name = cardSet.getName();
		final String abbreviation = cardSet.getAbbreviation();
		final String language = cardSet.getLanguage();
		final String cardsUrl = urlFactory.createEntityUrl(CardSet.class, cardSet.getId()) + "/cards";
		return new CardSetModel(url, name, abbreviation, language, cardsUrl);
	}
}