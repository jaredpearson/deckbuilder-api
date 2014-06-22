package deckbuilder.mtg.http.rest;

import static deckbuilder.mtg.http.rest.UserTestUtils.createTestUser;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;

import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.entities.User;
import deckbuilder.mtg.http.rest.models.DeckModel;
import deckbuilder.mtg.service.DeckService;

public class DeckIdResourceTest {

	@Test
	public void testGetById() throws Exception {
		User user = createTestUser();
		
		Deck deck = new Deck();
		deck.setId(1l);
		deck.setOwner(user);
		
		DeckService deckService = mock(DeckService.class);
		when(deckService.getDeckById(deck.getId())).thenReturn(deck);
		
		DeckIdResource resource = new DeckIdResource();
		resource.deckService = deckService;
		DeckModel actualDeck = resource.getDeckById(deck.getId());
		
		Assert.assertNotNull("Expected getDeckById to return the deck", deck);
		Assert.assertEquals("Expected the resource returned to be the one in returned by the service.", deck.getId().longValue(), actualDeck.getId());
	}
	
}
