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

import com.google.common.collect.Lists;
import com.google.inject.persist.Transactional;

import deckbuilder.mtg.entities.CardSet;
import deckbuilder.mtg.service.CardSetService;

@Path("/{version}/set")
@Produces(MediaType.APPLICATION_JSON)
public class CardSetController {
	@Inject
	EntityUrlFactory urlFactory;
	
	@Inject
	CardSetService cardSetService;
	
	@GET
	@Path("/{id}")
	public CardSetResource getCardSetById(@PathParam("id") Long id) throws Exception {
		final CardSet set = cardSetService.getCardSetById(id);
		return new CardSetResourceBuilder(urlFactory, set).build();
	}
	
	@GET
	@Path("/{id}/cards")
	public CardSetCardsResource getCardsForSet(@PathParam("id") Long id) throws Exception {
		final CardSet set = cardSetService.getCardSetById(id);
		return new CardSetCardsResourceBuilder(urlFactory, set).build();
	}
	
	@GET
	public CardSetListResource list() throws Exception {
		final List<CardSet> sets = cardSetService.getCardSets();
		final List<CardSetResource> resources = Lists.newArrayListWithExpectedSize(sets.size());
		for (CardSet cardSet : sets) {
			final CardSetResource resource = new CardSetResourceBuilder(urlFactory, cardSet).build();
			assert resource != null;
			resources.add(resource);
		}
		return new CardSetListResource(resources);
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
