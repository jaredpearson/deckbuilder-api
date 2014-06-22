package deckbuilder.mtg.http.rest;

import static org.mockito.Mockito.*;
import static deckbuilder.mtg.http.rest.SecurityTestUtils.*;
import static deckbuilder.mtg.http.rest.UserTestUtils.*;

import java.util.Arrays;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import deckbuilder.mtg.entities.CardSet;
import deckbuilder.mtg.entities.User;
import deckbuilder.mtg.http.rest.CardSetResource.CardSetCreateContext;
import deckbuilder.mtg.http.rest.CardSetResource.CardSetSaveResponse;
import deckbuilder.mtg.http.rest.models.CardSetListModel;
import deckbuilder.mtg.service.CardSetService;

public class CardSetControllerTest {
	
	@Test
	public void testList() throws Exception {
		CardSet cardSet1 = new CardSet();
		cardSet1.setId(1l);
		cardSet1.setName("Test");
		cardSet1.setAbbreviation("tst");
		cardSet1.setLanguage("en");
		
		CardSet cardSet2 = new CardSet();
		cardSet2.setId(2l);
		cardSet2.setName("Test2");
		cardSet2.setAbbreviation("ts2");
		cardSet2.setLanguage("en");
		
		CardSetService cardSetService = mock(CardSetService.class);
		when(cardSetService.getCardSets()).thenReturn(Arrays.asList(cardSet1, cardSet2));
		
		CardSetResource controller = new CardSetResource();
		controller.cardSetService = cardSetService;
		CardSetListModel resources = controller.list();
		
		Assert.assertNotNull("Expected the resources returned by list to not be null", resources);
		Assert.assertEquals("Expected there to be all of the resources returned", 2, resources.getCardSets().size());
	}
	
	@Test
	public void testCreateCardSetWithAdministrator() throws Exception {
		User testUser = createTestUser();
		SecurityContext securityContext = mockSecurityContext(testUser, true);
		
		CardSetService cardSetService = mock(CardSetService.class);
		when(cardSetService.createCardSet(any(CardSet.class))).thenAnswer(new Answer<CardSet>() {
			@Override
			public CardSet answer(InvocationOnMock invocation) throws Throwable {
				CardSet cardSet = (CardSet)invocation.getArguments()[0];
				cardSet.setId(1l);
				return cardSet;
			}
		});
		
		CardSetCreateContext cardSetData = new CardSetCreateContext();
		
		CardSetResource controller = new CardSetResource();
		controller.cardSetService = cardSetService;
		
		Response response = controller.createCardSet(cardSetData, securityContext);
		
		verify(cardSetService).createCardSet(any(CardSet.class));
		
		Assert.assertNotNull("Expected createCardSet to never return null", response);
		Assert.assertEquals("Expected the response to always be successful", 200, response.getStatus());
		
		CardSetSaveResponse cardSetSaveResponse = (CardSetSaveResponse) response.getEntity();
		Assert.assertNotNull("Expected the returned response to not be null", cardSetSaveResponse.getId());
	}
}
