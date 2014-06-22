package deckbuilder.mtg.http.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.google.inject.persist.UnitOfWork;

import deckbuilder.mtg.entities.Card;
import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.entities.DeckCard;
import deckbuilder.mtg.entities.User;
import deckbuilder.mtg.http.Principal;
import deckbuilder.mtg.http.rest.models.DeckModel;
import deckbuilder.mtg.http.rest.models.DeckModelBuilder;
import deckbuilder.mtg.service.CardService;
import deckbuilder.mtg.service.DeckService;
import deckbuilder.mtg.service.UserService;

@Path("/{version}/deck")
public class DeckResource {
	
	@Inject
	EntityUrlFactory urlFactory;
	
	@Inject
	DeckService deckService;
	
	@Inject
	UserService userService;
	
	@Inject
	CardService cardService;
	
	@Inject
	Provider<UnitOfWork> unitOfWork;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<DeckModel> list(@Context SecurityContext securityContext) throws Exception {
		final Principal principal = (Principal)securityContext.getUserPrincipal();
		final List<Deck> decks = deckService.getDecksForOwner(principal.getUser().getId());
		
		final ArrayList<DeckModel> resources = Lists.newArrayListWithExpectedSize(decks.size());
		for(Deck deck : decks) {
			final DeckModelBuilder builder = new DeckModelBuilder(urlFactory, deck);
			resources.add(builder.build());
		}
		return resources;
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response createDeck(DeckCreateModel deckData, @Context SecurityContext securityContext) throws Exception {
		final Principal principal = (Principal)securityContext.getUserPrincipal();
		
		// only let the administrator change the owner of a deck
		if(!securityContext.isUserInRole("administrator") && deckData.getOwner() != principal.getUser().getId()) {
			return Response.status(Status.FORBIDDEN).entity(new SaveResponse(false, new String[]{"Invalid owner. Only the current user can be specified as the owner."})).build();
		}
		
		final Deck deck = new Deck();
		
		if(deckData.getName() != null) {
			deck.setName(deckData.getName());
		}
		
		if(deckData.getOwner() != null) {
			User owner = userService.getUserById(deckData.getOwner());
			deck.setOwner(owner);
		} else {
			deck.setOwner(principal.getUser());
		}
		
		// first pass over the cards associated to the deck
		final List<Long> cardIds = Lists.newArrayListWithExpectedSize(deckData.getCards().size());
		for (DeckCardCreateModel deckCardData : deckData.getCards()) {
			
			// make sure the quantity is specified
			if (deckCardData.getQuantity() == null) {
				deckCardData.setQuantity(1);
			}
			
			if (deckCardData.getCardId() == null) {
				return Response.status(Status.BAD_REQUEST).entity(new SaveResponse(false, new String[]{"Card specified without card ID"})).build();
			}
			cardIds.add(deckCardData.getCardId());
		}
		
		// retrieve the cards that were specified in the input
		final List<Card> cards = cardService.getCardsById(cardIds);
		final Map<Long, Card> cardIdToCard = Maps.newHashMapWithExpectedSize(cards.size());
		for (Card card : cards) {
			cardIdToCard.put(card.getId(), card);
		}
		
		// second pass to check that all cards were loaded
		final List<DeckCard> deckCards = Lists.newArrayListWithExpectedSize(cards.size());
		for (DeckCardCreateModel deckCardData : deckData.getCards()) {
			final Card card = cardIdToCard.get(deckCardData.getCardId());
			if (card == null) {
				return Response.status(Status.BAD_REQUEST).entity(new SaveResponse(false, new String[]{"Unknown card ID specified: " + deckCardData.getCardId()})).build();
			}
			
			final DeckCard deckCard = new DeckCard();
			deckCard.setCard(card);
			deckCard.setDeck(deck);
			deckCards.add(deckCard);
		}
		deck.setCards(deckCards);
		
		// save the deck
		final Deck savedDeck = deckService.createDeck(deck);
		return Response.ok(new DeckSaveResponse(savedDeck.getId())).build();
	}
	
	public static class DeckCreateModel {
		private String name;
		private Long owner;
		private List<DeckCardCreateModel> cards = Lists.newArrayList();
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public Long getOwner() {
			return owner;
		}
		
		public void setOwner(Long owner) {
			this.owner = owner;
		}
		
		public void setCards(List<DeckCardCreateModel> cards) {
			this.cards = cards;
		}
		
		public List<DeckCardCreateModel> getCards() {
			return cards;
		}
	}
	
	public static class DeckCardCreateModel {
		private Integer quantity = 1;
		private Long cardId;
		
		public Long getCardId() {
			return cardId;
		}
		
		public void setCardId(Long cardId) {
			this.cardId = cardId;
		}
		
		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}
		
		public Integer getQuantity() {
			return quantity;
		}
	}
	
	public static class SaveResponse {
		private String[] messages;
		private Boolean success;
		
		public SaveResponse(Boolean success, String[] messages) {
			this.success = success;
			this.messages = messages;
		}
		
		public String[] getMessages() {
			return messages;
		}
		
		public Boolean getSuccess() {
			return success;
		}
	}
	
	public static class DeckSaveResponse extends SaveResponse {
		private Long id;
		
		public DeckSaveResponse(Long id) {
			super(true, null);
			this.id = id;
		}
		
		public Long getId() {
			return id;
		}
	}
}
