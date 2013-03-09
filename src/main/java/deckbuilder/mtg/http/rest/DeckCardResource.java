package deckbuilder.mtg.http.rest;

import deckbuilder.mtg.entities.DeckCard;

public class DeckCardResource extends Resource {
	private static final long serialVersionUID = 1L;
	
	private DeckCardResource() {
	}
	
	private void setSimpleProperties(DeckCard deckCard) {
		this.put("id", deckCard.getId());
		this.put("quantity", deckCard.getQuantity());
	}
	
	/**
	 * Creates a new DeckCardResource where the references are full resources.
	 */
	public static DeckCardResource create(DeckCard deckCard) {
		DeckCardResource resource = new DeckCardResource();
		resource.setSimpleProperties(deckCard);
		
		if(deckCard.getCard() != null) {
			resource.put("card", CardResource.createWithLinkedReferences(deckCard.getCard()));
		}
		
		if(deckCard.getDeck() != null) {
			resource.put("deck", DeckResource.createWithLinkedReferences(deckCard.getDeck()));
		}
		return resource;
	}
	
	/**
	 * Creates a new DeckCardResource where the references are links instead of full resources.
	 * @param deckCard The {@link DeckCard} to create a reference for
	 */
	public static DeckCardResource createWithLinkedReferences(DeckCard deckCard) {
		DeckCardResource resource = new DeckCardResource();
		resource.setSimpleProperties(deckCard);
		
		if(deckCard.getCard() != null) {
			resource.put("card", new CardLink(deckCard.getCard()));
		}
		
		if(deckCard.getDeck() != null) {
			resource.put("deck", new DeckLink(deckCard.getDeck()));
		}
		return resource;
	}
}
