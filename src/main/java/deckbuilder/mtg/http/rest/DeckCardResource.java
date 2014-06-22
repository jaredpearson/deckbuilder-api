package deckbuilder.mtg.http.rest;

import java.io.Serializable;

import javax.annotation.Nonnull;

public class DeckCardResource implements Serializable {
	private static final long serialVersionUID = -3698407646536889711L;
	private final String url;
	private final long id;
	private final int quantity;
	private final String cardUrl;
	private final String deckUrl;

	public DeckCardResource(@Nonnull String url, long id, int quantity, @Nonnull String cardUrl, @Nonnull String deckUrl) {
		assert url != null;
		this.url = url;
		this.id = id;
		this.quantity = quantity;
		this.cardUrl = cardUrl;
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
	
	public String getCardUrl() {
		return cardUrl;
	}
	
	public String getDeckUrl() {
		return deckUrl;
	}
}
