package deckbuilder.mtg.http.rest;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
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

import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.http.Principal;
import deckbuilder.mtg.http.rest.DeckResource.SaveResponse;
import deckbuilder.mtg.http.rest.models.DeckModel;
import deckbuilder.mtg.http.rest.models.DeckModelBuilder;
import deckbuilder.mtg.service.DeckService;

@Path("/{version}/deck/{id}")
public class DeckIdResource {
	
	@Inject
	EntityUrlFactory urlFactory;
	
	@Inject
	DeckService deckService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public DeckModel getDeckById(@PathParam("id") Long id) throws Exception {
		final Deck deck = deckService.getDeckById(id);
		return new DeckModelBuilder(urlFactory, deck).build();
	}

	@POST
	@Transactional
	public Response updateDeck(@PathParam("id") Long id, Deck deck, @Context SecurityContext securityContext) throws Exception {
		final Deck loadedDeck = deckService.getDeckById(id);
		
		//only the owner can update the deck
		Principal principal = (Principal)securityContext.getUserPrincipal();
		if(!securityContext.isUserInRole("administrator") && !loadedDeck.getOwner().equals(principal.getUser())) {
			return Response.status(Status.FORBIDDEN).entity(new SaveResponse(false, new String[]{"Invalid owner. Only the owner can update the deck."})).build();
		}
		
		deck.setId(id);
		deckService.updateDeck(deck);
		return Response.ok().build();
	}
	
	@DELETE
	@Transactional
	public Response deleteDeck(@PathParam("id") Long id, @Context SecurityContext securityContext) throws Exception {
		final Deck deck = deckService.getDeckById(id);
		if(deck != null) {
			//only the owner can update the deck
			Principal principal = (Principal)securityContext.getUserPrincipal();
			if(!securityContext.isUserInRole("administrator") && !deck.getOwner().equals(principal.getUser())) {
				return Response.status(Status.FORBIDDEN).entity(new SaveResponse(false, new String[]{"Invalid owner. Only the owner can delete a deck."})).build();
			}
			
			deckService.deleteDeck(deck.getId());
		}
		
		return Response.ok().build();
	}
	
	/**
	 * Builder of the URL for the {@link DeckIdResource}
	 * @author jared.pearson
	 */
	public static class UrlBuilder extends deckbuilder.mtg.http.rest.UrlBuilder {
		private final long id;
		
		public UrlBuilder(long id) {
			this.id = id;
		}
		
		@Override
		public String build() {
			return "/v1/deck/" + id;
		}
	}
}
