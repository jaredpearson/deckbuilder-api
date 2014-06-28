package deckbuilder.mtg.http.rest;

import static org.mockito.Mockito.*;
import static deckbuilder.mtg.http.rest.SecurityTestUtils.*;
import static deckbuilder.mtg.http.rest.UserTestUtils.*;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.entities.User;
import deckbuilder.mtg.http.rest.Builder.BuildContext;
import deckbuilder.mtg.http.rest.DeckResource.DeckCreateModel;
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
		deck.setName("Test");
		
		DeckService deckService = mock(DeckService.class);
		when(deckService.getDecksForOwner(user.getId())).thenReturn(Arrays.asList(deck));

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
		
		SecurityContext securityContext = mockSecurityContext(user);
		
		DeckResource resource = new DeckResource();
		resource.deckService = deckService;
		resource.buildContextFactory = buildContextFactory;
		resource.urlFactory = urlFactory;
		List<DeckModel> decks = resource.list(securityContext, uriInfo);
		
		Assert.assertEquals("Expected list to return all available decks", 1, decks.size());
		Assert.assertEquals("Expected the resource returned to be the one in returned by the service.", deck.getId().longValue(), decks.get(0).getId());
	}
	
	@Test
	public void testCreateDeck() throws Exception {
		User user = createTestUser();
		
		SecurityContext securityContext = mockSecurityContext(user);

		DeckCreateModel deckData = new DeckCreateModel();
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
	
}
