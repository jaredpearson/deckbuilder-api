package deckbuilder.mtg.http.rest;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

/**
 * Builder for instances of {@link CardSetListResource}
 * @author jared.pearson
 */
public class CardSetListResourceBuilder implements Builder<CardSetListResource> {
	private final List<CardSetResourceBuilder> cardSets;
	
	public CardSetListResourceBuilder(@Nonnull List<CardSetResourceBuilder> cardSets) {
		assert cardSets != null;
		this.cardSets = cardSets;
	}
	
	/**
	 * Builds a list of {@link CardSetResource} instances
	 */
	@Override
	public CardSetListResource build() {
		final List<CardSetResource> resources = Lists.newArrayListWithExpectedSize(cardSets.size());
		for (CardSetResourceBuilder setBuilder : cardSets) {
			final CardSetResource cardSet = setBuilder.build();
			assert cardSet != null;
			resources.add(cardSet);
		}
		return new CardSetListResource(resources);
	}
}
