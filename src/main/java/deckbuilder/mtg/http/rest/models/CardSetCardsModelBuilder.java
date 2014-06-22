package deckbuilder.mtg.http.rest.models;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import deckbuilder.mtg.entities.Card;
import deckbuilder.mtg.entities.CardSet;
import deckbuilder.mtg.http.rest.Builder;
import deckbuilder.mtg.http.rest.EntityUrlFactory;

/**
 * Builder for {@link CardSetCardsModel} instances
 * @author jared.pearson
 */
public class CardSetCardsModelBuilder implements Builder<CardSetCardsModel> {
	private final EntityUrlFactory urlFactory;
	private final CardSet cardSet;
	
	public CardSetCardsModelBuilder(@Nonnull EntityUrlFactory urlFactory, @Nonnull CardSet cardSet) {
		assert urlFactory != null;
		assert cardSet != null;
		this.urlFactory = urlFactory;
		this.cardSet = cardSet;
	}

	@Override
	public CardSetCardsModel build() {
		final List<Card> cards = cardSet.getCards();
		final ArrayList<CardModel> resources = Lists.newArrayListWithExpectedSize(cards.size());
		for(Card card : cards) {
			final CardModelBuilder resourceBuilder = new CardModelBuilder(urlFactory, card);
			resources.add(resourceBuilder.build());
		}
		return new CardSetCardsModel(resources);
	}
}
