package deckbuilder.mtg.http.rest.models;

import javax.annotation.Nonnull;

import deckbuilder.mtg.entities.Card;
import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.entities.DeckCard;
import deckbuilder.mtg.http.rest.Builder;
import deckbuilder.mtg.http.rest.EntityUrlFactory;

/**
 * Creates a new DeckCardResource where the references are full resources.
 */
public class DeckCardModelBuilder implements Builder<DeckCardModel> {
	private final EntityUrlFactory urlFactory;
	private final DeckCard deckCard;
	
	public DeckCardModelBuilder(@Nonnull EntityUrlFactory urlFactory, @Nonnull DeckCard deckCard) {
		assert urlFactory != null;
		assert deckCard != null;
		this.urlFactory = urlFactory;
		this.deckCard = deckCard;
	}
	
	@Override
	public DeckCardModel build(BuildContext context) {
		final String url = urlFactory.createEntityUrl(DeckCard.class, deckCard.getId()).build(context);
		final long id = deckCard.getId();
		final int quantity = (deckCard.getQuantity() == null) ? 1 : deckCard.getQuantity();
		final String cardUrl = urlFactory.createEntityUrl(Card.class, deckCard.getCard().getId()).build(context);
		final String deckUrl = urlFactory.createEntityUrl(Deck.class, deckCard.getDeck().getId()).build(context);
		return new DeckCardModel(url, id, quantity, cardUrl, deckUrl);
	}
}