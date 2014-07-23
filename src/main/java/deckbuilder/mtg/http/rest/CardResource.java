package deckbuilder.mtg.http.rest;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.springframework.transaction.annotation.Transactional;

import deckbuilder.mtg.entities.Card;
import deckbuilder.mtg.entities.CardSet;
import deckbuilder.mtg.service.CardService;
import deckbuilder.mtg.service.CardSetService;

@Singleton
@Path("/{version}/card")
@Produces(MediaType.APPLICATION_JSON)
@Transactional(readOnly=true)
public class CardResource {
	
	@Inject
	EntityUrlFactory urlFactory;
	
	@Inject
	CardService cardService;
	
	@Inject
	CardSetService cardSetService;
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly=false)
	public Response createCard(CardCreateModel cardData, @Context SecurityContext securityContext) throws Exception {
		//only administrators can create cards
		if(!securityContext.isUserInRole("administrator")) {
			return Response.status(Status.FORBIDDEN).build();
		}
		
		Card card = new Card();
		
		if(cardData.getSet() != null) {
			CardSet cardSet = cardSetService.getCardSetById(cardData.getSet());
			card.setSet(cardSet);
		}
		
		card.setName(cardData.getName());
		card.setSetIndex(cardData.getSetIndex());
		
		card = cardService.createCard(card);
		return Response.ok(new CardSaveContext(card.getId())).build();
	}

	/**
	 * Data transfer object where the references are simple properties to ID values 
	 * @author jared.pearson
	 */
	public static class CardCreateModel {
		private String name;
		private Long set;
		private String setIndex;
		
		public void setName(String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
		
		public void setSet(Long set) {
			this.set = set;
		}
		public Long getSet() {
			return set;
		}
		
		public String getSetIndex() {
			return setIndex;
		}
		public void setSetIndex(String setIndex) {
			this.setIndex = setIndex;
		}
	}
	
	public static class CardSaveContext extends SaveResponse {
		private Long id;
		
		public CardSaveContext(Long id) {
			super(true, null);
			this.id = id;
		}
		
		public Long getId() {
			return id;
		}
	}
}
