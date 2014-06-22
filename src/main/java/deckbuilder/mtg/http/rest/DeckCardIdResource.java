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

@Path("/{version}/deckCard/{id}")
public class DeckCardIdResource {
	@Inject
	EntityUrlFactory urlFactory;
	
	@Inject
	DeckCardService deckCardService;
	
	@Inject
	BuildContextFactory buildContextFactory;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public DeckCardModel getDeckCardById(@Context UriInfo uriInfo, @PathParam("id") Long deckCardId) throws Exception {
		final DeckCard deckCard = deckCardService.getDeckCardById(deckCardId);
		final BuildContext context = buildContextFactory.create(uriInfo);
		return new DeckCardModelBuilder(urlFactory, deckCard).build(context);
	}
	
	/**
	 * Builder used to create URLs for the {@link DeckCardIdResource}
	 * @author jared.pearson
	 */
	public static class UrlBuilder extends deckbuilder.mtg.http.rest.UrlBuilder {
		private final long deckCardId;
		
		public UrlBuilder(long deckCardId) {
			this.deckCardId = deckCardId;
		}
		
		@Override
		protected String buildPath(BuildContext context) {
			return "/v1/deckCard/" + deckCardId;
		}
	}
}
