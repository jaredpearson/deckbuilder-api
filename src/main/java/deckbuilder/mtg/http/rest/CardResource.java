package deckbuilder.mtg.http.rest;

import java.io.Serializable;

import deckbuilder.mtg.entities.Card;

/**
 * Resource for displaying {@link Card} entities
 * @author jared.pearson
 */
public class CardResource implements Serializable {
	private static final long serialVersionUID = 1L;

	private final String url;
	private final String name;
	private final String castingCost;
	private final String powerToughness;
	private final String typeLine;
	private final String rarity;
	private final String text;
	private final String setIndex;
	private final String author;
	private final String cardSetUrl;
	
	public CardResource(
			final String url,
			final String name,
			final String castingCost,
			final String powerToughness,
			final String typeLine,
			final String rarity,
			final String text,
			final String setIndex,
			final String author, 
			final String cardSetUrl) {
		this.url = url;
		this.name = name;
		this.castingCost = castingCost;
		this.powerToughness = powerToughness;
		this.typeLine = typeLine;
		this.rarity = rarity;
		this.text = text;
		this.setIndex = setIndex;
		this.author = author;
		this.cardSetUrl = cardSetUrl;
	}
	
	/**
	 * Gets the URL of this resource
	 */
	public String getUrl() {
		return url;
	}
	
	public String getName() {
		return name;
	}
	
	public String getCastingCost() {
		return castingCost;
	}
	
	public String getPowerToughness() {
		return powerToughness;
	}
	public String getTypeLine() {
		return typeLine;
	}
	
	public String getRarity() {
		return rarity;
	}
	
	public String getText() {
		return text;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getSetIndex() {
		return setIndex;
	}
	
	/**
	 * Gets the URL of the card set associated to this card
	 */
	public String getCardSetUrl() {
		return cardSetUrl;
	}
	
}