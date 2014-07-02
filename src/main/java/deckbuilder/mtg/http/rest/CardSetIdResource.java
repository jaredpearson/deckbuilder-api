package deckbuilder.mtg.http.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import deckbuilder.mtg.entities.CardSet;
import deckbuilder.mtg.http.rest.Builder.BuildContext;
import deckbuilder.mtg.http.rest.models.CardSetModel;
import deckbuilder.mtg.http.rest.models.CardSetModelBuilder;
import deckbuilder.mtg.service.CardSetService;

@Path("/{version}/set/{id}")
public class CardSetIdResource {

	@Inject
	EntityUrlFactory urlFactory;
	
	@Inject
	CardSetService cardSetService;
	
	@Inject
	BuildContextFactory buildContextFactory;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public CardSetModel getCardSetById(@Context UriInfo uriInfo, @PathParam("id") Long id) throws Exception {
		final CardSet set = cardSetService.getCardSetById(id);
		final BuildContext context = buildContextFactory.create(uriInfo);
		return new CardSetModelBuilder(urlFactory, set).build(context);
	}

	/**
	 * Creates the URL for the {@link CardSetIdResource}
	 * @author jared.pearson
	 */
	public static class UrlBuilder extends deckbuilder.mtg.http.rest.UrlBuilder {
		private final long id;
		
		public UrlBuilder(long id) {
			this.id = id;
		}
		
		@Override
		protected String buildPath(BuildContext context) {
			return "/v1/set/" + id;
		}
	}
}
