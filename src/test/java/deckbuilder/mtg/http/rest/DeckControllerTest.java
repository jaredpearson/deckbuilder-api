package deckbuilder.mtg.http.rest;

import static org.mockito.Mockito.*;
import static deckbuilder.mtg.http.rest.SecurityTestUtils.*;
import static deckbuilder.mtg.http.rest.UserTestUtils.*;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.entities.User;
import deckbuilder.mtg.http.rest.DeckResource.DeckCreateContext;
import deckbuilder.mtg.http.rest.DeckResource.DeckSaveResponse;
import deckbuilder.mtg.http.rest.models.DeckModel;
import deckbuilder.mtg.service.DeckService;
import deckbuilder.mtg.service.UserService;

public class DeckControllerTest {
	@Test
	public void testList() throws Exception {
		User user = createTestUser();
		
		Deck deck = new Deck();
		deck.setId(1l);
		deck.setOwner(user);
		
		DeckService deckService = mock(DeckService.class);
		when(deckService.getDecksForOwner(user.getId())).thenReturn(Arrays.asList(deck));
		
		SecurityContext securityContext = mockSecurityContext(user);
		
		DeckResource controller = new DeckResource();
		controller.deckService = deckService;
		List<DeckModel> decks = controller.list(securityContext);
		
		Assert.assertEquals("Expected list to return all available decks", 1, decks.size());
		Assert.assertEquals("Expected the resource returned to be the one in returned by the service.", deck.getId().longValue(), decks.get(0).getId());
	}
	
	@Test
	public void testGetById() throws Exception {
		User user = createTestUser();
		
		Deck deck = new Deck();
		deck.setId(1l);
		deck.setOwner(user);
		
		DeckService deckService = mock(DeckService.class);
		when(deckService.getDeckById(deck.getId())).thenReturn(deck);
		
		DeckResource controller = new DeckResource();
		controller.deckService = deckService;
		DeckModel actualDeck = controller.getDeckById(deck.getId());
		
		Assert.assertNotNull("Expected getDeckById to return the deck", deck);
		Assert.assertEquals("Expected the resource returned to be the one in returned by the service.", deck.getId().longValue(), actualDeck.getId());
	}
	
	@Test
	public void testCreateDeck() throws Exception {
		User user = createTestUser();
		
		SecurityContext securityContext = mockSecurityContext(user);

		DeckCreateContext deckData = new DeckCreateContext();
		deckData.setName("Test");
		deckData.setOwner(user.getId());
		
		DeckService deckService = mock(DeckService.class);
		when(deckService.createDeck(any(Deck.class))).thenAnswer(new Answer<Deck>() {
			@Override
			public Deck answer(InvocationOnMock invocation) throws Throwable {
				Deck deck = (Deck)invocation.getArguments()[0];
				deck.setId(1l);
				return deck;
			}
		});
		
		UserService userService = mock(UserService.class);
		
		DeckResource controller = new DeckResource();
		controller.deckService = deckService;
		controller.userService = userService;
		Response saveResponse = controller.createDeck(deckData, securityContext);
		
		Assert.assertNotNull("Expected createDeck to return a save response.", saveResponse);
		Assert.assertTrue("Expected createDeck to return a success save response", saveResponse.getEntity() instanceof DeckSaveResponse);
		
		Long newDeckId = ((DeckSaveResponse)saveResponse.getEntity()).getId();
		Assert.assertNotNull("Expected the save response to return the deck ID", newDeckId);
		
		verify(deckService).createDeck(any(Deck.class));
	}
	
	@Test
	public void testUpdateDeck() throws Exception {
		User owner = createTestUser();
		
		Deck deck = new Deck();
		deck.setId(1l);
		deck.setOwner(owner);
		
		SecurityContext securityContext = mockSecurityContext(owner);
		DeckService deckService = mock(DeckService.class);
		when(deckService.getDeckById(deck.getId())).thenReturn(deck);
		
		//change the deck name
		deck.setName("New Name!");
		
		DeckResource controller = new DeckResource();
		controller.deckService = deckService;
		controller.updateDeck(deck.getId(), deck, securityContext);
		
		verify(deckService).updateDeck(any(Deck.class));
	}
	
	@Test()
	public void testUpdateDeckCanOnlyBeCalledByOwner() throws Exception {
		User owner = createTestUser();
		User currentUser = createTestUser();
		currentUser.setId(2l);
		
		Deck deck = new Deck();
		deck.setId(1l);
		deck.setOwner(owner);
		
		SecurityContext securityContext = mockSecurityContext(currentUser, false);
		DeckService deckService = mock(DeckService.class);
		when(deckService.getDeckById(deck.getId())).thenReturn(deck);
		
		//change the deck name
		deck.setName("New Name!");
		
		DeckResource controller = new DeckResource();
		controller.deckService = deckService;
		Response response = controller.updateDeck(deck.getId(), deck, securityContext);
		
		Assert.assertNotNull("Expected the response to not be null", response);
		Assert.assertEquals("Expected the response to 403 Forbidden because the current user is not the owner of the deck.", 403, response.getStatus());
	}
	
	@Test
	public void testDeleteDeck() throws Exception {
		User owner = createTestUser();
		
		Deck deck = new Deck();
		deck.setId(1l);
		deck.setOwner(owner);
		
		SecurityContext securityContext = mockSecurityContext(owner);
		DeckService deckService = mock(DeckService.class);
		when(deckService.getDeckById(deck.getId())).thenReturn(deck);
		
		DeckResource controller = new DeckResource();
		controller.deckService = deckService;
		controller.deleteDeck(deck.getId(), securityContext);
		
		verify(deckService).deleteDeck(deck.getId());
	}
	
	@Test
	public void testDeleteDeckCanOnlyBeDeletedByOwner() throws Exception {
		User owner = createTestUser();
		User currentUser = createTestUser();
		currentUser.setId(2l);
		
		Deck deck = new Deck();
		deck.setId(1l);
		deck.setOwner(owner);
		
		SecurityContext securityContext = mockSecurityContext(currentUser, false);
		DeckService deckService = mock(DeckService.class);
		when(deckService.getDeckById(deck.getId())).thenReturn(deck);
		
		DeckResource controller = new DeckResource();
		controller.deckService = deckService;
		Response response = controller.deleteDeck(deck.getId(), securityContext);
		
		Assert.assertNotNull("Response should not be null", response);
		Assert.assertEquals("Expected a 403 Forbidden to be thrown when the user tries to modify a deck they do not own.", 403, response.getStatus());
	}
}
