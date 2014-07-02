package deckbuilder.mtg.http.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
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
import deckbuilder.mtg.http.rest.Builder.BuildContext;
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
	BuildContextFactory buildContextFactory;
	
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
	public @Nonnull List<DeckModel> list(@Context SecurityContext securityContext, @Context UriInfo uriInfo) throws Exception {
		assert securityContext != null;
		assert uriInfo != null;
		final Principal principal = (Principal)securityContext.getUserPrincipal();
		final BuildContext context = buildContextFactory.create(uriInfo);
		final List<Deck> decks = deckService.getDecksForOwner(principal.getUser().getId());
		
		final ArrayList<DeckModel> resources = Lists.newArrayListWithExpectedSize(decks.size());
		for(Deck deck : decks) {
			final DeckModelBuilder builder = new DeckModelBuilder(urlFactory, deck);
			resources.add(builder.build(context));
		}
		return resources;
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public @Nonnull Response createDeck(DeckCreateModel deckData, @Context SecurityContext securityContext) throws Exception {
		assert deckData != null;
		assert securityContext != null;
		final Principal principal = (Principal)securityContext.getUserPrincipal();
		
		// only let the administrator change the owner of a deck
		if(!securityContext.isUserInRole("administrator") && deckData.getOwner() != principal.getUser().getId()) {
			return Response.status(Status.FORBIDDEN).entity(SaveResponse.fail("Invalid owner. Only the current user can be specified as the owner.")).build();
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
		
		if (deckData.getCards() != null && !deckData.getCards().isEmpty()) {
			// first pass over the cards associated to the deck
			final List<Long> cardIds = Lists.newArrayListWithExpectedSize(deckData.getCards().size());
			for (DeckCardCreateModel deckCardData : deckData.getCards()) {
				
				// make sure the quantity is specified
				if (deckCardData.getQuantity() == null) {
					deckCardData.setQuantity(1);
				}
				
				if (deckCardData.getCardId() == null) {
					return Response.status(Status.BAD_REQUEST).entity(SaveResponse.fail("Card specified without card ID")).build();
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
					return Response.status(Status.BAD_REQUEST).entity(SaveResponse.fail("Unknown card ID specified: " + deckCardData.getCardId())).build();
				}
				
				final DeckCard deckCard = new DeckCard();
				deckCard.setCard(card);
				deckCard.setDeck(deck);
				deckCards.add(deckCard);
			}
			deck.setCards(deckCards);
		}
		
		// save the deck
		final Deck savedDeck = deckService.createDeck(deck);
		return Response.ok(new DeckSaveResponse(savedDeck.getId())).build();
	}
	
	/**
	 * Input model for when a user wants to create a new {@link Deck}
	 * @author jared.pearson
	 */
	public static class DeckCreateModel {
		private String name;
		private Long owner;
		private List<DeckCardCreateModel> cards = Lists.newArrayList();
		
		public @Nullable String getName() {
			return name;
		}
		
		public void setName(@Nullable String name) {
			this.name = name;
		}
		
		public @Nullable Long getOwner() {
			return owner;
		}
		
		public void setOwner(@Nullable Long owner) {
			this.owner = owner;
		}
		
		public void setCards(@Nullable List<DeckCardCreateModel> cards) {
			this.cards = cards;
		}
		
		public @Nullable List<DeckCardCreateModel> getCards() {
			return cards;
		}
	}
	
	/**
	 * Input model for when a user wants to create a new {@link DeckCard}
	 * @author jared.pearson
	 */
	public static class DeckCardCreateModel {
		private Integer quantity = 1;
		private Long cardId;
		
		public @Nullable Long getCardId() {
			return cardId;
		}
		
		public void setCardId(@Nullable Long cardId) {
			this.cardId = cardId;
		}
		
		public void setQuantity(@Nullable Integer quantity) {
			this.quantity = quantity;
		}
		
		public @Nullable Integer getQuantity() {
			return quantity;
		}
	}
	
	/**
	 * Response for a save operation
	 * @author jared.pearson
	 */
	public static class SaveResponse {
		private final String[] messages;
		private final Boolean success;
		
		public SaveResponse(Boolean success, String[] messages) {
			this.success = success;
			this.messages = messages;
		}
		
		/**
		 * Gets any messages that were caused by the save operation or null if none
		 * were added. If the success was false, there should be at least one message.
		 */
		public @Nullable String[] getMessages() {
			return messages;
		}
		
		/**
		 * Gets the success status of the save
		 */
		public Boolean getSuccess() {
			return success;
		}
		
		/**
		 * Creates a new save response that represents a fail.
		 */
		public static @Nonnull SaveResponse fail(@Nonnull String... messages) {
			assert messages != null;
			assert messages.length > 0;
			return new SaveResponse(false, messages);
		}
	}
	
	/**
	 * Response for when a new Deck is saved 
	 * @author jared.pearson
	 */
	public static class DeckSaveResponse extends SaveResponse {
		private Long id;
		
		public DeckSaveResponse(Long id) {
			super(true, null);
			this.id = id;
		}
		
		/**
		 * Gets the ID of the new deck
		 */
		public Long getId() {
			return id;
		}
	}
	
	/**
	 * Builds URL instances for the {@link DeckResource}
	 * @author jared.pearson
	 */
	public static class UrlBuilder extends deckbuilder.mtg.http.rest.UrlBuilder {
		@Override
		protected String buildPath(BuildContext context) {
			return "/v1/deck";
		}
	}
}
