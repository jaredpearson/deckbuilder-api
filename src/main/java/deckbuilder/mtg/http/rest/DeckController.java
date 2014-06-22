package deckbuilder.mtg.http.rest;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import com.google.common.collect.Lists;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.google.inject.persist.UnitOfWork;

import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.entities.User;
import deckbuilder.mtg.http.Principal;
import deckbuilder.mtg.service.DeckService;
import deckbuilder.mtg.service.UserService;

@Path("/{version}/deck")
public class DeckController {
	
	@Inject
	EntityUrlFactory urlFactory;
	
	@Inject
	DeckService deckService;
	
	@Inject
	UserService userService;
	
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
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public DeckModel getDeckById(@PathParam("id") Long id) throws Exception {
		final Deck deck = deckService.getDeckById(id);
		return new DeckModelBuilder(urlFactory, deck).build();
	}
	
	@GET
	@Path("/{id}/cards")
	@Produces(MediaType.APPLICATION_JSON)
	public DeckCardsModel getCardsForDeck(@PathParam("id") Long id) throws Exception {
		final Deck deck = deckService.getDeckById(id);
		return new DeckCardsModelBuilder(urlFactory, deck).build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response createDeck(DeckCreateContext deckData, @Context SecurityContext securityContext) throws Exception {
		Principal principal = (Principal)securityContext.getUserPrincipal();
		if(!securityContext.isUserInRole("administrator") && deckData.getOwner() != principal.getUser().getId()) {
			return Response.status(Status.FORBIDDEN).entity(new SaveResponse(false, new String[]{"Invalid owner. Only the current user can be specified as the owner."})).build();
		}
		
		Deck deck = new Deck();
		
		if(deckData.getName() != null) {
			deck.setName(deckData.getName());
		}
		
		if(deckData.getOwner() != null) {
			User owner = userService.getUserById(deckData.getOwner());
			deck.setOwner(owner);
		} else {
			deck.setOwner(principal.getUser());
		}
		
		deck = deckService.createDeck(deck);
		return Response.ok(new DeckSaveResponse(deck.getId())).build();
	}
	
	@POST
	@Path("/{id}")
	@Transactional
	public Response updateDeck(@PathParam("id") Long id, Deck deck, @Context SecurityContext securityContext) throws Exception {
		final Deck loadedDeck = deckService.getDeckById(id);
		
		//only the owner can update the deck
		Principal principal = (Principal)securityContext.getUserPrincipal();
		if(!securityContext.isUserInRole("administrator") && !loadedDeck.getOwner().equals(principal.getUser())) {
			return Response.status(Status.FORBIDDEN).entity(new SaveResponse(false, new String[]{"Invalid owner. Only the owner can update the deck."})).build();
		}
		
		deck.setId(id);
		deckService.updateDeck(deck);
		return Response.ok().build();
	}
	
	@DELETE
	@Path("/{id}")
	@Transactional
	public Response deleteDeck(@PathParam("id") Long id, @Context SecurityContext securityContext) throws Exception {
		final Deck deck = deckService.getDeckById(id);
		if(deck != null) {
			//only the owner can update the deck
			Principal principal = (Principal)securityContext.getUserPrincipal();
			if(!securityContext.isUserInRole("administrator") && !deck.getOwner().equals(principal.getUser())) {
				return Response.status(Status.FORBIDDEN).entity(new SaveResponse(false, new String[]{"Invalid owner. Only the owner can delete a deck."})).build();
			}
			
			deckService.deleteDeck(deck.getId());
		}
		
		return Response.ok().build();
	}
	
	public static class DeckCreateContext {
		private String name;
		private Long owner;
		
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
