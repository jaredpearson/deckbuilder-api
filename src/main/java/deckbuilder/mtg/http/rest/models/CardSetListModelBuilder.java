package deckbuilder.mtg.http.rest.models;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import deckbuilder.mtg.http.rest.Builder;

/**
 * Builder for instances of {@link CardSetListModel}
 * @author jared.pearson
 */
public class CardSetListModelBuilder implements Builder<CardSetListModel> {
	private final List<CardSetModelBuilder> cardSets;
	
	public CardSetListModelBuilder(@Nonnull List<CardSetModelBuilder> cardSets) {
		assert cardSets != null;
		this.cardSets = cardSets;
	}
	
	/**
	 * Builds a list of {@link CardSetModel} instances
	 */
	@Override
	public CardSetListModel build(BuildContext context) {
		final List<CardSetModel> resources = Lists.newArrayListWithExpectedSize(cardSets.size());
		for (CardSetModelBuilder setBuilder : cardSets) {
			final CardSetModel cardSet = setBuilder.build(context);
			assert cardSet != null;
			resources.add(cardSet);
		}
		return new CardSetListModel(resources);
	}
}
