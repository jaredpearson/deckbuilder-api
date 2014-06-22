package deckbuilder.mtg.http.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import deckbuilder.mtg.entities.Deck;
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
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public DeckModel getDeckById(@PathParam("id") Long id) throws Exception {
		final Deck deck = deckService.getDeckById(id);
		return new DeckModelBuilder(urlFactory, deck).build();
	}
	
}
