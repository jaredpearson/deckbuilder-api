package deckbuilder.mtg.http.rest;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import deckbuilder.mtg.entities.Card;
import deckbuilder.mtg.entities.CardSet;

/**
 * Builder for {@link CardSetCardsResource} instances
 * @author jared.pearson
 */
public class CardSetCardsResourceBuilder implements Builder<CardSetCardsResource> {
	private final EntityUrlFactory urlFactory;
	private final CardSet cardSet;
	
	public CardSetCardsResourceBuilder(@Nonnull EntityUrlFactory urlFactory, @Nonnull CardSet cardSet) {
		assert urlFactory != null;
		assert cardSet != null;
		this.urlFactory = urlFactory;
		this.cardSet = cardSet;
	}

	@Override
	public CardSetCardsResource build() {
		final List<Card> cards = cardSet.getCards();
		final ArrayList<CardResource> resources = Lists.newArrayListWithExpectedSize(cards.size());
		for(Card card : cards) {
			final CardResourceBuilder resourceBuilder = new CardResourceBuilder(urlFactory, card);
			resources.add(resourceBuilder.build());
		}
		return new CardSetCardsResource(resources);
	}
}
