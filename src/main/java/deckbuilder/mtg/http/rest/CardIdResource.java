package deckbuilder.mtg.http.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import deckbuilder.mtg.entities.Card;
import deckbuilder.mtg.http.rest.Builder.BuildContext;
import deckbuilder.mtg.http.rest.models.CardModel;
import deckbuilder.mtg.http.rest.models.CardModelBuilder;
import deckbuilder.mtg.service.CardService;

@Path("/{version}/card/{id}")
public class CardIdResource {

	@Inject
	EntityUrlFactory urlFactory;
	
	@Inject
	CardService cardService;
	
	@Inject
	BuildContextFactory buildContextFactory;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public CardModel getCardById(@Context UriInfo uriInfo, @PathParam("id") long id) throws Exception {
		final Card card = cardService.getCardById(id);
		final BuildContext buildContext = buildContextFactory.create(uriInfo);
		return new CardModelBuilder(urlFactory, card).build(buildContext);
	}
	
	/**
	 * Builds the URL for the {@link CardIdResource}
	 * @author jared.pearson
	 */
	public static class UrlBuilder extends deckbuilder.mtg.http.rest.UrlBuilder {
		private long id;
		
		public UrlBuilder(long id) {
			this.id = id;
		}
		
		@Override
		protected String buildPath(BuildContext context) {
			return "/v1/card/" + id;
		}
	}
}
