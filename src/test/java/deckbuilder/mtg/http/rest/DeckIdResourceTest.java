package deckbuilder.mtg.http.rest;

import static deckbuilder.mtg.http.rest.SecurityTestUtils.mockSecurityContext;
import static deckbuilder.mtg.http.rest.UserTestUtils.createTestUser;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.junit.Assert;
import org.junit.Test;

import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.entities.User;
import deckbuilder.mtg.http.rest.Builder.BuildContext;
import deckbuilder.mtg.http.rest.models.DeckModel;
import deckbuilder.mtg.service.DeckService;

public class DeckIdResourceTest {

	@Test
	public void testGetById() throws Exception {
		User user = createTestUser();
		
		Deck deck = new Deck();
		deck.setId(1l);
		deck.setOwner(user);
		deck.setName("Test");
		
		DeckService deckService = mock(DeckService.class);
		when(deckService.getDeckById(deck.getId())).thenReturn(deck);

		UriInfo uriInfo = mock(UriInfo.class);
		URI requestUri = new URI("http://test.com");
		BuildContext buildContext = mock(BuildContext.class);
		when(buildContext.getRequestUri()).thenReturn(requestUri);
		BuildContextFactory buildContextFactory = mock(BuildContextFactory.class);
		when(buildContextFactory.create(uriInfo)).thenReturn(buildContext);
		UrlBuilder urlBuilder = mock(UrlBuilder.class);
		when(urlBuilder.build(buildContext)).thenReturn("http://test.com");
		EntityUrlFactory urlFactory = mock(EntityUrlFactory.class);
		when(urlFactory.createEntityUrl(any(Class.class), any())).thenReturn(urlBuilder);
		
		DeckIdResource resource = new DeckIdResource();
		resource.deckService = deckService;
		resource.buildContextFactory = buildContextFactory;
		resource.urlFactory = urlFactory;
		DeckModel actualDeck = resource.getDeckById(uriInfo, deck.getId());
		
		Assert.assertNotNull("Expected getDeckById to return the deck", deck);
		Assert.assertEquals("Expected the resource returned to be the one in returned by the service.", deck.getId().longValue(), actualDeck.getId());
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
		
		DeckIdResource resource = new DeckIdResource();
		resource.deckService = deckService;
		Response response = resource.updateDeck(deck.getId(), deck, securityContext);
		
		verify(deckService).updateDeck(any(Deck.class));
		Assert.assertNotNull("Expected the response to not be null", response);
		Assert.assertEquals("Expected the response to be 200 OK", 200, response.getStatus());
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
		
		DeckIdResource resource = new DeckIdResource();
		resource.deckService = deckService;
		Response response = resource.updateDeck(deck.getId(), deck, securityContext);
		
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
		
		DeckIdResource resource = new DeckIdResource();
		resource.deckService = deckService;
		Response response = resource.deleteDeck(deck.getId(), securityContext);
		
		verify(deckService).deleteDeck(deck.getId());
		Assert.assertNotNull("Expected the response to not be null", response);
		Assert.assertEquals("Expected the response to be 200 OK", 200, response.getStatus());
		Assert.assertNull("Expected the response entity to be null", response.getEntity());
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
		
		DeckIdResource resource = new DeckIdResource();
		resource.deckService = deckService;
		Response response = resource.deleteDeck(deck.getId(), securityContext);
		
		Assert.assertNotNull("Response should not be null", response);
		Assert.assertEquals("Expected a 403 Forbidden to be thrown when the user tries to modify a deck they do not own.", 403, response.getStatus());
	}
}
