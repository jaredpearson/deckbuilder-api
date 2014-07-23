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
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.http.rest.Builder.BuildContext;
import deckbuilder.mtg.http.rest.models.DeckModel;
import deckbuilder.mtg.http.rest.models.DeckModelBuilder;
import deckbuilder.mtg.service.DeckService;

@Path("/{version}/deck/{id}")
@Transactional(readOnly=true)
public class DeckIdResource {
	
	@Autowired
	EntityUrlFactory urlFactory;
	
	@Inject
	DeckService deckService;
	
	@Inject
	BuildContextFactory buildContextFactory;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public DeckModel getDeckById(@Context UriInfo uriInfo, @PathParam("id") Long id) throws Exception {
		final Deck deck = deckService.getDeckById(id);
		final BuildContext context = buildContextFactory.create(uriInfo);
		return new DeckModelBuilder(urlFactory, deck).build(context);
	}

	@POST
	@Transactional(readOnly=false)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateDeck(@PathParam("id") Long id, Deck deck, @Context SecurityContext securityContext) throws Exception {
		final Deck loadedDeck = deckService.getDeckById(id);
		
		//only the owner can update the deck
		DeckHelper.assertOwnerOrAdmin(loadedDeck, securityContext);
		
		deck.setId(id);
		deckService.updateDeck(deck);
		return Response.ok(SaveResponse.success()).build();
	}
	
	@DELETE
	@Transactional(readOnly=false)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteDeck(@PathParam("id") Long id, @Context SecurityContext securityContext) throws Exception {
		final Deck deck = deckService.getDeckById(id);
		if(deck != null) {
			//only the owner can update the deck
			DeckHelper.assertOwnerOrAdmin(deck, securityContext);
			
			deckService.deleteDeck(deck.getId());
		}
		
		return Response.ok(SaveResponse.success()).build();
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
		protected String buildPath(BuildContext context) {
			return "/v1/deck/" + id;
		}
	}
}
