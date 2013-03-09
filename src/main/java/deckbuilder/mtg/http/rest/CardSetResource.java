package deckbuilder.mtg.http.rest;

import java.util.ArrayList;
import java.util.List;

import deckbuilder.mtg.entities.CardSet;

public class CardSetResource extends Resource {
	private static final long serialVersionUID = -4482212674496033845L;
	
	private CardSetResource() {
	}
	
	private void setSimpleProperties(CardSet cardSet) {
		this.put("name", cardSet.getName());
		this.put("abbreviation", cardSet.getAbbreviation());
		this.put("language", cardSet.getLanguage());
	}

	public static CardSetResource create(CardSet cardSet) {
		CardSetResource resource = new CardSetResource();
		resource.setSimpleProperties(cardSet);
		resource.put("cards", CardResource.createWithLinkedReferences(cardSet.getCards()));
		return resource;
	}
	
	public static CardSetResource createWithLinkedReferences(CardSet cardSet) {
		CardSetResource resource = new CardSetResource();
		resource.setSimpleProperties(cardSet);
		resource.put("cards", CardLink.create(cardSet.getCards()));
		return resource;
	}
	
	public static List<CardSetResource> create(List<CardSet> cardSets) {
		ArrayList<CardSetResource> resources = new ArrayList<>(cardSets.size());
		for(CardSet cardSet : cardSets) {
			resources.add(create(cardSet));
		}
		return resources;
	}
}
