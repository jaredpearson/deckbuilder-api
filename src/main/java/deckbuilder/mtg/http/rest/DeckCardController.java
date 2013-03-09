package deckbuilder.mtg.http.rest;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.inject.persist.Transactional;

import deckbuilder.mtg.entities.Card;
import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.entities.DeckCard;
import deckbuilder.mtg.service.CardService;
import deckbuilder.mtg.service.DeckCardService;
import deckbuilder.mtg.service.DeckService;

@Path("/{version}/deckCard")
public class DeckCardController {
	
	@Inject
	CardService cardService;
	
	@Inject
	DeckService deckService;
	
	@Inject
	DeckCardService deckCardService;
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public DeckCardSaveContext createDeckCard(DeckCardCreateContext deckCardData) throws Exception {
		DeckCard deckCard = new DeckCard();
		
		Card card = cardService.getCardById(deckCardData.getCard());
		deckCard.setCard(card);
		
		Deck deck = deckService.getDeckById(deckCardData.getDeck());
		deckCard.setDeck(deck);
		
		if(deckCardData.getQuantity() != null) {
			deckCard.setQuantity(deckCardData.getQuantity());
		}
		
		deckCardService.createDeckCard(deckCard);
		return new DeckCardSaveContext(deckCard.getId());
	}
	
	public static class DeckCardSaveContext {
		private Long id;
		
		public DeckCardSaveContext(Long id) {
			this.id = id;
		}
		
		public Long getId() {
			return id;
		}
	}
	
	/**
	 * Data transfer object where the references are simple properties to ID values 
	 * @author jared.pearson
	 */
	public static class DeckCardCreateContext {
		private Long id;
		private Long card;
		private Long deck;
		private Integer quantity;
		
		public DeckCardCreateContext() {
		}
		
		public Long getId() {
			return id;
		}
		
		public void setId(Long id) {
			this.id = id;
		}
		
		public Long getCard() {
			return card;
		}
		
		public void setCard(Long card) {
			this.card = card;
		}
		
		public Long getDeck() {
			return deck;
		}
		
		public void setDeck(Long deck) {
			this.deck = deck;
		}
		
		public Integer getQuantity() {
			return quantity;
		}
		
		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}
	}
}
