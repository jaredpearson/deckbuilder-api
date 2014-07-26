package deckbuilder.mtg.http.rest.models;

import java.io.Serializable;

import javax.annotation.Nonnull;

public class DeckCardModel implements Serializable {
	private static final long serialVersionUID = -3698407646536889711L;
	private final String url;
	private final long id;
	private final int quantity;
	private final CardModel card;
	private final String deckUrl;

	public DeckCardModel(@Nonnull String url, long id, int quantity, @Nonnull CardModel card, @Nonnull String deckUrl) {
		assert url != null;
		this.url = url;
		this.id = id;
		this.quantity = quantity;
		this.card = card;
		this.deckUrl = deckUrl;
	}
	
	public String getUrl() {
		return url;
	}
	
	public long getId() {
		return id;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public CardModel getCard() {
		return card;
	}
	
	public String getDeckUrl() {
		return deckUrl;
	}
}
