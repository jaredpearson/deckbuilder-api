package deckbuilder.mtg.http.rest;

import javax.inject.Inject;
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

import com.google.inject.persist.Transactional;

import deckbuilder.mtg.entities.Card;
import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.entities.DeckCard;
import deckbuilder.mtg.http.rest.Builder.BuildContext;
import deckbuilder.mtg.http.rest.models.DeckCardsModel;
import deckbuilder.mtg.http.rest.models.DeckCardsModelBuilder;
import deckbuilder.mtg.service.CardService;
import deckbuilder.mtg.service.DeckCardService;
import deckbuilder.mtg.service.DeckService;

@Path("/{version}/deck/{id}/cards")
public class DeckIdCardsResource {
	
	@Inject
	EntityUrlFactory urlFactory;
	
	@Inject
	DeckService deckService;
	
	@Inject
	CardService cardService;
	
	@Inject 
	DeckCardService deckCardService;
	
	@Inject
	BuildContextFactory buildContextFactory;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public DeckCardsModel getCardsForDeck(@Context UriInfo uriInfo, @PathParam("id") Long id) throws Exception {
		final Deck deck = deckService.getDeckById(id);
		final BuildContext context = buildContextFactory.create(uriInfo);
		return new DeckCardsModelBuilder(urlFactory, deck).build(context);
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response createDeckCard(
			DeckCardCreateModel deckCardData, 
			@PathParam("id") Long deckId,
			@Context SecurityContext securityContext) throws Exception {
		
		// TODO: make sure the deck card isn't already created
		final Deck deck = deckService.getDeckById(deckId);
		
		//only the owner (or admin) can update the deck
		DeckHelper.assertOwnerOrAdmin(deck, securityContext);
		
		// TODO: make sure the card ID is specified
		final Card card = cardService.getCardById(deckCardData.getCard());

		final DeckCard deckCard = new DeckCard();
		deckCard.setDeck(deck);
		deckCard.setCard(card);
		
		if(deckCardData.getQuantity() != null) {
			deckCard.setQuantity(deckCardData.getQuantity());
		}
		
		deckCardService.createDeckCard(deckCard);
		return Response.ok(new DeckCardSaveContext(deckCard.getId())).build();
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

	public static class DeckCardSaveContext extends SaveResponse {
		private final Long id;
		
		public DeckCardSaveContext(Long id) {
			super(true, null);
			this.id = id;
		}
		
		public Long getId() {
			return id;
		}
	}

	/**
	 * Data transfer object where the references are simple properties to ID values 
	 * @author jared.pearson
	 */
	public static class DeckCardCreateModel {
		private Long id;
		private Long card;
		private Long deck;
		private Integer quantity;
		
		public DeckCardCreateModel() {
		}
		
		public Long getId() {
			return id;
		}
		
		public void setId(Long id) {
			this.id = id;
		}
		
		public Long getCard() {
			return card;
		}
		
		public void setCard(Long card) {
			this.card = card;
		}
		
		public Long getDeck() {
			return deck;
		}
		
		public void setDeck(Long deck) {
			this.deck = deck;
		}
		
		public Integer getQuantity() {
			return quantity;
		}
		
		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}
	}
}
