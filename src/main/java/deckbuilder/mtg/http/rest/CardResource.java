package deckbuilder.mtg.http.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import deckbuilder.mtg.entities.Card;

public class CardResource extends Resource {
	private static final long serialVersionUID = 1L;
	
	private CardResource() {
	}
	
	private void setSimpleProperties(Card card) {
		this.put("id", card.getId());
		this.put("name", card.getName());
	}
	
	public static CardResource create(Card card) {
		CardResource resource = new CardResource();
		resource.setSimpleProperties(card);
		
		if(card.getSet() != null) {
			resource.put("set", CardSetResource.create(card.getSet()));
		}
		
		return resource;
	}
	
	public static List<CardResource> create(List<Card> cards) {
		ArrayList<CardResource> resources = new ArrayList<>(cards.size());
		for(Card card : cards) {
			resources.add(create(card));
		}
		return resources;
	}
	
	public static CardResource createWithLinkedReferences(Card card) {
		CardResource resource = new CardResource();
		resource.setSimpleProperties(card);
		
		if(card.getSet() != null) {
			resource.put("set", new CardSetLink(card.getSet()));
		}
		
		return resource;
	}
	
	public static List<CardResource> createWithLinkedReferences(List<Card> cards) {
		if(cards == null || cards.isEmpty()) {
			return Collections.emptyList();
		}
		ArrayList<CardResource> resources = new ArrayList<>(cards.size());
		for(Card card : cards) {
			resources.add(createWithLinkedReferences(card));
		}
		return resources;
	}
}