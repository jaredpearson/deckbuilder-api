package deckbuilder.mtg.http.rest.models;

import java.io.Serializable;

import javax.annotation.Nonnull;

public class DeckModel implements Serializable {
	private static final long serialVersionUID = -257383003130744950L;
	private final String url;
	private final long id;
	private final String name;
	private final String cardsUrl;
	private final String ownerUrl;
	
	public DeckModel(
			@Nonnull String url, 
			long id, 
			@Nonnull String name, 
			@Nonnull String cardsUrl, 
			@Nonnull String ownerUrl) {
		assert url != null;
		assert name != null;
		assert cardsUrl != null;
		assert ownerUrl != null;
		this.url = url;
		this.id = id;
		this.name = name;
		this.cardsUrl = cardsUrl;
		this.ownerUrl = ownerUrl;
	}
	
	/**
	 * Gets the URL of this
	 */
	public String getUrl() {
		return url;
	}
	
	public long getId() {
		return id;
	}
	
	/**
	 * Gets the name of the deck
	 */
	public String getName() {
		return name;
	}
	
	public String getCardsUrl() {
		return cardsUrl;
	}
	
	public String getOwnerUrl() {
		return ownerUrl;
	}
}