package deckbuilder.mtg.http.rest.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.entities.DeckCard;
import deckbuilder.mtg.http.rest.Builder;
import deckbuilder.mtg.http.rest.DeckIdCardsResource;
import deckbuilder.mtg.http.rest.EntityUrlFactory;
import deckbuilder.mtg.http.rest.UrlBuilder;

/**
 * Builder for creating instances of {@link DeckCardsModel}.
 * @author jared.pearson
 */
public class DeckCardsModelBuilder implements Builder<DeckCardsModel> {
	private final UrlBuilder url;
	private final List<DeckCardModelBuilder> cards;
	
	/**
	 * Creates the list of deck cards from the given deck
	 */
	public DeckCardsModelBuilder(@Nonnull EntityUrlFactory urlFactory, @Nonnull Deck deck) {
		assert urlFactory != null;
		assert deck != null;
		
		// create the URL builder
		this.url = new DeckIdCardsResource.UrlBuilder(deck.getId());

		// create the card builders
		final ArrayList<DeckCardModelBuilder> cards = Lists.newArrayListWithExpectedSize(deck.getCards().size());
		for (DeckCard deckCard : deck.getCards()) {
			final DeckCardModelBuilder deckCardResource = new DeckCardModelBuilder(urlFactory, deckCard);
			cards.add(deckCardResource);
		}
		this.cards = Collections.unmodifiableList(cards);
		
	}
	
	@Override
	public DeckCardsModel build(BuildContext context) {
		final String url = this.url.build(context);
		
		final List<DeckCardModel> cards = Lists.newArrayListWithCapacity(this.cards.size());
		for (DeckCardModelBuilder deckCardBuilder : this.cards) {
			DeckCardModel deckCardModel = deckCardBuilder.build(context);
			assert deckCardModel != null : deckCardBuilder.getClass().getName() + ".build() returned null";
			cards.add(deckCardModel);
		}
		
		return new DeckCardsModel(url, cards);
	}
}
