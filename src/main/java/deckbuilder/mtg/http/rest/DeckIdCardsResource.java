package deckbuilder.mtg.http.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.http.rest.Builder.BuildContext;
import deckbuilder.mtg.http.rest.models.DeckCardsModel;
import deckbuilder.mtg.http.rest.models.DeckCardsModelBuilder;
import deckbuilder.mtg.service.DeckService;

@Path("/{version}/deck/{id}/cards")
public class DeckIdCardsResource {
	
	@Inject
	EntityUrlFactory urlFactory;
	
	@Inject
	DeckService deckService;
	
	@Inject
	BuildContextFactory buildContextFactory;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public DeckCardsModel getCardsForDeck(@Context UriInfo uriInfo, @PathParam("id") Long id) throws Exception {
		final Deck deck = deckService.getDeckById(id);
		final BuildContext context = buildContextFactory.create(uriInfo);
		return new DeckCardsModelBuilder(urlFactory, deck).build(context);
	}
	
	/**
	 * Builds URL instances for the {@link DeckIdCardsResource}
	 * @author jared.pearson
	 */
	public static class UrlBuilder extends deckbuilder.mtg.http.rest.UrlBuilder {
		private final long id;
		
		public UrlBuilder(long id) {
			this.id = id;
		}
		
		@Override
		protected String buildPath(BuildContext context) {
			return "/v1/deck/" + id + "/cards";
		}
	}
}
