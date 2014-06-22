package deckbuilder.mtg.http.rest;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.entities.DeckCard;

/**
 * Builder for creating instances of {@link DeckCardsResource}.
 * @author jared.pearson
 *
 */
public class DeckCardsResourceBuilder implements Builder<DeckCardsResource> {
	private final EntityUrlFactory urlFactory;
	private final Deck deck;
	
	public DeckCardsResourceBuilder(@Nonnull EntityUrlFactory urlFactory, @Nonnull Deck deck) {
		assert urlFactory != null;
		assert deck != null;
		this.urlFactory = urlFactory;
		this.deck = deck;
	}
	
	@Override
	public DeckCardsResource build() {
		final String url = urlFactory.createEntityUrl(Deck.class, deck.getId()) + "/cards";
		
		final ArrayList<DeckCardResource> cards = Lists.newArrayListWithExpectedSize(deck.getCards().size());
		for (DeckCard deckCard : deck.getCards()) {
			final DeckCardResource deckCardResource = new DeckCardResourceBuilder(urlFactory, deckCard).build();
			assert deckCardResource != null;
			cards.add(deckCardResource);
		}
		
		return new DeckCardsResource(url, cards);
	}
}
