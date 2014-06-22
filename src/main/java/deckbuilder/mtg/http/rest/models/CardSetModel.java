package deckbuilder.mtg.http.rest.models;

import java.io.Serializable;

import javax.annotation.Nonnull;

import deckbuilder.mtg.entities.CardSet;

/**
 * Output resource for a {@link CardSet}
 * @author jared.pearson
 */
public class CardSetModel implements Serializable {
	private static final long serialVersionUID = -4482212674496033845L;
	private final String url;
	private final String name;
	private final String abbreviation;
	private final String language;
	private final String cardsUrl;
	
	public CardSetModel(String url, String name, String abbreviation, String language, @Nonnull String cardsUrl) {
		this.url = url;
		this.name = name;
		this.abbreviation = abbreviation;
		this.language = language;
		this.cardsUrl = cardsUrl;
	}
	
	/**
	 * Gets the URL to this {@link CardSetModel}
	 */
	public String getUrl() {
		return url;
	}
	
	public String getName() {
		return name;
	}
	
	public String getAbbreviation() {
		return abbreviation;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public String getCardsUrl() {
		return cardsUrl;
	}
}
