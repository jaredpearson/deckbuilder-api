package deckbuilder.mtg.http.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.http.rest.models.DeckCardsModel;
import deckbuilder.mtg.http.rest.models.DeckCardsModelBuilder;
import deckbuilder.mtg.service.DeckService;

@Path("/{version}/deck/{id}/cards")
public class DeckIdCardsResource {
	
	@Inject
	EntityUrlFactory urlFactory;
	
	@Inject
	DeckService deckService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public DeckCardsModel getCardsForDeck(@PathParam("id") Long id) throws Exception {
		final Deck deck = deckService.getDeckById(id);
		return new DeckCardsModelBuilder(urlFactory, deck).build();
	}
	
}