package deckbuilder.mtg.http.rest;

import java.util.ArrayList;

import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.entities.DeckCard;

public class DeckResource extends Resource {
	private static final long serialVersionUID = 1L;

	private DeckResource() {
	}
	
	private void setSimpleProperties(Deck deck) {
		this.put("id", deck.getId());
		this.put("name", deck.getName());
	}
	
	/**
	 * Creates a {@link DeckResource} in which all of the references are full resources.
	 */
	public static DeckResource create(Deck deck) {
		DeckResource deckResource = new DeckResource();
		deckResource.setSimpleProperties(deck);
		
		if(deck.getOwner() != null) {
			deckResource.put("owner", new UserResource(deck.getOwner()));
		}
		
		if(deck.getCards() != null) {
			ArrayList<DeckCardResource> deckCardResources = new ArrayList<>();
			for(DeckCard deckCard : deck.getCards()) {
				deckCardResources.add(DeckCardResource.createWithLinkedReferences(deckCard));
			}
			deckResource.put("cards", deckCardResources);
		}
		return deckResource;
	}
	
	/**
	 * Creates a new DeckResource where the references are links instead of full resources.
	 */
	public static DeckResource createWithLinkedReferences(Deck deck) {
		DeckResource deckResource = new DeckResource();
		deckResource.setSimpleProperties(deck);
		
		if(deck.getOwner() != null) {
			deckResource.put("owner", new UserLink(deck.getOwner()));
		}
		
		if(deck.getCards() != null) {
			ArrayList<DeckCardLink> deckCardResources = new ArrayList<>();
			for(DeckCard deckCard : deck.getCards()) {
				deckCardResources.add(new DeckCardLink(deckCard));
			}
			deckResource.put("cards", deckCardResources);
		}
		return deckResource;
	}
}