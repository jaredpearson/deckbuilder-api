package deckbuilder.mtg.http.rest;

import javax.annotation.Nonnull;

import deckbuilder.mtg.entities.Card;
import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.entities.DeckCard;

/**
 * Creates a new DeckCardResource where the references are full resources.
 */
public class DeckCardResourceBuilder implements Builder<DeckCardResource> {
	private final EntityUrlFactory urlFactory;
	private final DeckCard deckCard;
	
	public DeckCardResourceBuilder(@Nonnull EntityUrlFactory urlFactory, @Nonnull DeckCard deckCard) {
		assert urlFactory != null;
		assert deckCard != null;
		this.urlFactory = urlFactory;
		this.deckCard = deckCard;
	}
	
	@Override
	public DeckCardResource build() {
		final String url = urlFactory.createEntityUrl(DeckCard.class, deckCard.getId());
		final long id = deckCard.getId();
		final int quantity = (deckCard.getQuantity() == null) ? 1 : deckCard.getQuantity();
		final String cardUrl = urlFactory.createEntityUrl(Card.class, deckCard.getCard().getId());
		final String deckUrl = urlFactory.createEntityUrl(Deck.class, deckCard.getDeck().getId());
		return new DeckCardResource(url, id, quantity, cardUrl, deckUrl);
	}
}