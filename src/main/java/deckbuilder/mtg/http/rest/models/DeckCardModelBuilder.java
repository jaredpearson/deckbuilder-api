package deckbuilder.mtg.http.rest.models;

import javax.annotation.Nonnull;

import deckbuilder.mtg.entities.Card;
import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.entities.DeckCard;
import deckbuilder.mtg.http.rest.Builder;
import deckbuilder.mtg.http.rest.DeckIdCardsIdResource;
import deckbuilder.mtg.http.rest.EntityUrlFactory;
import deckbuilder.mtg.http.rest.UrlBuilder;

/**
 * Creates a new DeckCardResource where the references are full resources.
 */
public class DeckCardModelBuilder implements Builder<DeckCardModel> {
	private final UrlBuilder url;
	private final long id;
	private final int quantity;
	private final UrlBuilder cardUrl;
	private final UrlBuilder deckUrl;
	
	/**
	 * Creates a new deck card model builder with the given deck card instance
	 */
	public DeckCardModelBuilder(@Nonnull EntityUrlFactory urlFactory, @Nonnull DeckCard deckCard) {
		assert urlFactory != null;
		assert deckCard != null;
		this.url = new DeckIdCardsIdResource.UrlBuilder(deckCard.getDeck().getId(), deckCard.getCard().getId());
		this.id = deckCard.getId();
		this.quantity = (deckCard.getQuantity() == null) ? 1 : deckCard.getQuantity();
		this.cardUrl = urlFactory.createEntityUrl(Card.class, deckCard.getCard().getId());
		this.deckUrl = urlFactory.createEntityUrl(Deck.class, deckCard.getDeck().getId());
	}
	
	@Override
	public DeckCardModel build(BuildContext context) {
		final String url = this.url.build(context);
		final String cardUrl = this.cardUrl.build(context);
		final String deckUrl = this.deckUrl.build(context);
		return new DeckCardModel(url, this.id, this.quantity, cardUrl, deckUrl);
	}
}