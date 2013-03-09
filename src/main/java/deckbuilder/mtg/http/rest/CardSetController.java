package deckbuilder.mtg.http.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;

import com.google.inject.persist.Transactional;

import deckbuilder.mtg.entities.CardSet;
import deckbuilder.mtg.service.CardSetService;

@Path("/{version}/set")
@Produces(MediaType.APPLICATION_JSON)
public class CardSetController {
	
	@Inject
	CardSetService cardSetService;
	
	@GET
	@Path("/{id}")
	public CardSetResource getCardSetById(@PathParam("id") Long id) throws Exception {
		CardSet set = cardSetService.getCardSetById(id);
		return CardSetResource.create(set);
	}
	
	@GET
	@Path("/{id}/cards")
	public List<CardResource> getCardsForSet(@PathParam("id") Long id) throws Exception {
		CardSet set = cardSetService.getCardSetById(id);
		return CardResource.createWithLinkedReferences(set.getCards());
	}
	
	@GET
	public List<CardSetResource> list() throws Exception {
		List<CardSet> sets = cardSetService.getCardSets();
		return CardSetResource.create(sets);
	}
	
	@POST
	@Transactional
	public Response createCardSet(CardSetCreateContext cardSetData, @Context SecurityContext securityContext) throws Exception {
		
		//only administrators can create card sets
		if(!securityContext.isUserInRole("administrator")) {
			return Response.status(Status.FORBIDDEN).build();
		}
		
		CardSet cardSet = new CardSet();
		cardSet.setName(cardSetData.getName());
		
		cardSet = cardSetService.createCardSet(cardSet);
		
		return Response.ok(new CardSetSaveResponse(cardSet.getId())).build();
	}
	
	public static class CardSetSaveResponse {
		private Long id;
		
		public CardSetSaveResponse(Long id) {
			this.id = id;
		}
		
		public Long getId() {
			return id;
		}
	}
	
	public static class CardSetCreateContext {
		private String name;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
}
