package deckbuilder.mtg.http.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import deckbuilder.mtg.entities.CardSet;
import deckbuilder.mtg.http.rest.models.CardSetCardsModel;
import deckbuilder.mtg.http.rest.models.CardSetCardsModelBuilder;
import deckbuilder.mtg.service.CardSetService;

@Path("/{version}/set/{id}/cards")
public class CardSetIdCardsResource {

	@Inject
	EntityUrlFactory urlFactory;
	
	@Inject
	CardSetService cardSetService;
	
	@GET
	public CardSetCardsModel getCardsForSet(@PathParam("id") Long id) throws Exception {
		final CardSet set = cardSetService.getCardSetById(id);
		return new CardSetCardsModelBuilder(urlFactory, set).build();
	}
	
	/**
	 * Creates the URL for the {@link CardSetIdCardsResource}
	 * @author jared.pearson
	 */
	public static class UrlBuilder extends deckbuilder.mtg.http.rest.UrlBuilder {
		private final long id;
		
		public UrlBuilder(long id) {
			this.id = id;
		}
		
		@Override
		public String build() {
			return "/v1/set/" + id + "/cards";
		}
	}
}
