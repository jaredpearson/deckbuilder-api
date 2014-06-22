package deckbuilder.mtg.http.rest.models;

import javax.annotation.Nonnull;

import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.entities.User;
import deckbuilder.mtg.http.rest.Builder;
import deckbuilder.mtg.http.rest.UrlBuilder;
import deckbuilder.mtg.http.rest.DeckIdCardsResource;
import deckbuilder.mtg.http.rest.EntityUrlFactory;

/**
 * Builder for {@link DeckModel} instances
 * @author jared.pearson
 */
public class DeckModelBuilder implements Builder<DeckModel> {
	private UrlBuilder url;
	private Long id;
	private String name;
	private UrlBuilder cardsUrl;
	private UrlBuilder ownerUrl;
	
	public DeckModelBuilder() {
	}
	
	public DeckModelBuilder(@Nonnull EntityUrlFactory urlFactory, @Nonnull Deck deck) {
		assert urlFactory != null;
		assert deck != null;

		this.url = urlFactory.createEntityUrl(Deck.class, deck.getId());
		this.id = deck.getId();
		this.name = deck.getName();
		this.cardsUrl = new DeckIdCardsResource.UrlBuilder(deck.getId());
		this.ownerUrl = urlFactory.createEntityUrl(User.class, deck.getOwner().getId());
	}
	
	public UrlBuilder getUrl() {
		return url;
	}
	
	public void setUrl(UrlBuilder urlBuilder) {
		this.url = urlBuilder;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UrlBuilder getCardsUrl() {
		return cardsUrl;
	}

	public void setCardsUrl(UrlBuilder cardsUrl) {
		this.cardsUrl = cardsUrl;
	}
	
	public UrlBuilder getOwnerUrl() {
		return ownerUrl;
	}

	public void setOwnerUrl(UrlBuilder ownerUrl) {
		this.ownerUrl = ownerUrl;
	}

	public DeckModel build(BuildContext context) {
		String url = this.url.build(context);
		String cardsUrl = this.cardsUrl.build(context);
		String ownerUrl = this.ownerUrl.build(context);
		return new DeckModel(url, id, name, cardsUrl, ownerUrl);
	}
}