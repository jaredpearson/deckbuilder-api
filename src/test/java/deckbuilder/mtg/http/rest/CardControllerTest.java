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
import deckbuilder.mtg.entities.CardSet;
import deckbuilder.mtg.entities.User;
import deckbuilder.mtg.http.rest.CardController.CardCreateContext;
import deckbuilder.mtg.http.rest.CardController.CardSaveContext;
import deckbuilder.mtg.service.CardService;
import deckbuilder.mtg.service.CardSetService;

public class CardControllerTest {
	
	@Test
	public void testGetCardById() throws Exception {
		CardSet cardSet = new CardSet();
		cardSet.setName("Set");
		cardSet.setLanguage("en");
		cardSet.setAbbreviation("set");
		
		Card card = new Card();
		card.setId(1l);
		card.setName("Test");
		card.setSet(cardSet);
		
		CardService cardService = mock(CardService.class);
		when(cardService.getCardById(card.getId())).thenReturn(card);
		
		CardController controller = new CardController();
		controller.cardService = cardService;
		
		CardResource actualCard = controller.getCardById(card.getId());
		
		Assert.assertNotNull("Expected getCardById to not return null", actualCard);
		Assert.assertEquals("Expected the card resource to include the ID of the card", card.getId(), actualCard.get("id"));
		Assert.assertEquals("Expected the card resource to include the name of the card", card.getName(), actualCard.get("name"));
	}
	
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
		
		CardController controller = new CardController();
		controller.cardService = cardService;
		controller.cardSetService = cardSetService;
		
		CardCreateContext cardData = new CardCreateContext();
		cardData.setName("test");
		
		Response response = controller.createCard(cardData, securityContext);
		Assert.assertNotNull("Expected createCard to never return null", response);
		Assert.assertEquals("Expected the response to be ok since the user is an administrator", 200, response.getStatus());
		
		CardSaveContext saveContext = (CardSaveContext)response.getEntity();
		Assert.assertEquals("Expected the ID to be in the returned entity", Long.valueOf(1l), saveContext.getId());
	}
}
