package deckbuilder.mtg.http.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import deckbuilder.mtg.entities.DeckCard;
import deckbuilder.mtg.http.rest.Builder.BuildContext;
import deckbuilder.mtg.http.rest.models.DeckCardModel;
import deckbuilder.mtg.http.rest.models.DeckCardModelBuilder;
import deckbuilder.mtg.service.DeckCardService;

@Path("/{version}/deck/{deckId}/cards/{cardId}")
public class DeckIdCardsIdResource {

	@Inject
	EntityUrlFactory urlFactory;
	
	@Inject
	DeckCardService deckCardService;
	
	@Inject
	BuildContextFactory buildContextFactory;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public DeckCardModel getDeckCardById(@Context UriInfo uriInfo, @PathParam("deckId") Long deckId, @PathParam("cardId") Long cardId) throws Exception {
		final DeckCard deckCard = deckCardService.getDeckCardByDeckIdAndCardId(deckId, cardId);
		final BuildContext context = buildContextFactory.create(uriInfo);
		return new DeckCardModelBuilder(urlFactory, deckCard).build(context);
	}
	
	/**
	 * Builds URL instances for the {@link DeckIdCardsIdResource}
	 * @author jared.pearson
	 */
	public static class UrlBuilder extends deckbuilder.mtg.http.rest.UrlBuilder {
		private final long deckId;
		private final long cardId;
		
		public UrlBuilder(long deckId, long cardId) {
			this.deckId = deckId;
			this.cardId = cardId;
		}
		
		@Override
		protected String buildPath(BuildContext context) {
			return "/v1/deck/" + this.deckId + "/cards/" + this.cardId;
		}
	}
	
}
