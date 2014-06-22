package deckbuilder.mtg.http.rest.models;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.entities.DeckCard;
import deckbuilder.mtg.http.rest.Builder;
import deckbuilder.mtg.http.rest.DeckIdCardsResource;
import deckbuilder.mtg.http.rest.EntityUrlFactory;

/**
 * Builder for creating instances of {@link DeckCardsModel}.
 * @author jared.pearson
 *
 */
public class DeckCardsModelBuilder implements Builder<DeckCardsModel> {
	private final EntityUrlFactory urlFactory;
	private final Deck deck;
	
	public DeckCardsModelBuilder(@Nonnull EntityUrlFactory urlFactory, @Nonnull Deck deck) {
		assert urlFactory != null;
		assert deck != null;
		this.urlFactory = urlFactory;
		this.deck = deck;
	}
	
	@Override
	public DeckCardsModel build(BuildContext context) {
		final String url = new DeckIdCardsResource.UrlBuilder(deck.getId()).build(context);
		
		final ArrayList<DeckCardModel> cards = Lists.newArrayListWithExpectedSize(deck.getCards().size());
		for (DeckCard deckCard : deck.getCards()) {
			final DeckCardModel deckCardResource = new DeckCardModelBuilder(urlFactory, deckCard).build(context);
			assert deckCardResource != null;
			cards.add(deckCardResource);
		}
		
		return new DeckCardsModel(url, cards);
	}
}
