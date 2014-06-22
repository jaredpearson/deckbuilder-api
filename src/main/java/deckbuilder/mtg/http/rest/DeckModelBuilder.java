package deckbuilder.mtg.http.rest;

import javax.annotation.Nonnull;

import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.entities.User;

/**
 * Builder for {@link DeckModel} instances
 * @author jared.pearson
 */
public class DeckModelBuilder implements Builder<DeckModel> {
	private final EntityUrlFactory urlFactory;
	private final Deck deck;
	
	public DeckModelBuilder(@Nonnull EntityUrlFactory urlFactory, @Nonnull Deck deck) {
		assert urlFactory != null;
		assert deck != null;
		this.urlFactory = urlFactory;
		this.deck = deck;
	}
	
	public DeckModel build() {
		final String url = urlFactory.createEntityUrl(Deck.class, deck.getId());
		final long id = deck.getId();
		final String name = deck.getName();
		final String cardsUrl = url + "/cards";
		final String ownerUrl = urlFactory.createEntityUrl(User.class, deck.getOwner().getId());
		return new DeckModel(url, id, name, cardsUrl, ownerUrl);
	}
}