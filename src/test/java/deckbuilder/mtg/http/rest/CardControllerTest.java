package deckbuilder.mtg.http.rest;

import static org.mockito.Mockito.*;
import static deckbuilder.mtg.http.rest.SecurityTestUtils.*;
import static deckbuilder.mtg.http.rest.UserTestUtils.*;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import deckbuilder.mtg.entities.Card;
import deckbuilder.mtg.entities.User;
import deckbuilder.mtg.http.rest.CardResource.CardCreateModel;
import deckbuilder.mtg.http.rest.CardResource.CardSaveContext;
import deckbuilder.mtg.service.CardService;
import deckbuilder.mtg.service.CardSetService;

public class CardControllerTest {
	
	@Test
	public void testCreateCard() throws Exception {
		User testUser = createTestUser();
		SecurityContext securityContext = mockSecurityContext(testUser, true);
		
		CardService cardService = mock(CardService.class);
		when(cardService.createCard(any(Card.class))).thenAnswer(new Answer<Card>(){
			@Override
			public Card answer(InvocationOnMock invocation) throws Throwable {
				Card card = (Card)invocation.getArguments()[0];
				card.setId(1l);
				return card;
			}
		});
		
		CardSetService cardSetService = mock(CardSetService.class);
		
		CardResource controller = new CardResource();
		controller.cardService = cardService;
		controller.cardSetService = cardSetService;
		
		CardCreateModel cardData = new CardCreateModel();
		cardData.setName("test");
		
		Response response = controller.createCard(cardData, securityContext);
		Assert.assertNotNull("Expected createCard to never return null", response);
		Assert.assertEquals("Expected the response to be ok since the user is an administrator", 200, response.getStatus());
		
		CardSaveContext saveContext = (CardSaveContext)response.getEntity();
		Assert.assertEquals("Expected the ID to be in the returned entity", Long.valueOf(1l), saveContext.getId());
	}
}
