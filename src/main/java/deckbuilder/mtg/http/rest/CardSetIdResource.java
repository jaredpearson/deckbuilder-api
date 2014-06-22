package deckbuilder.mtg.http.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import deckbuilder.mtg.entities.CardSet;
import deckbuilder.mtg.http.rest.models.CardSetModel;
import deckbuilder.mtg.http.rest.models.CardSetModelBuilder;
import deckbuilder.mtg.service.CardSetService;

@Path("/{version}/set/{id}")
public class CardSetIdResource {

	@Inject
	EntityUrlFactory urlFactory;
	
	@Inject
	CardSetService cardSetService;
	
	@GET
	@Path("/{id}")
	public CardSetModel getCardSetById(@PathParam("id") Long id) throws Exception {
		final CardSet set = cardSetService.getCardSetById(id);
		return new CardSetModelBuilder(urlFactory, set).build();
	}
	
	public static class UrlBuilder extends deckbuilder.mtg.http.rest.UrlBuilder {
		private long id;
		
		public UrlBuilder(long id) {
			this.id = id;
		}
		
		@Override
		public String build() {
			return "/v1/set/" + id;
		}
	}
}
