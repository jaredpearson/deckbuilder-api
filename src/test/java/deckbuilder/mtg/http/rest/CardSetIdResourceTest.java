package deckbuilder.mtg.http.rest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;

import deckbuilder.mtg.entities.CardSet;
import deckbuilder.mtg.http.rest.models.CardSetModel;
import deckbuilder.mtg.service.CardSetService;

public class CardSetIdResourceTest {

	@Test
	public void testGetById() throws Exception {
		CardSet cardSet = new CardSet();
		cardSet.setId(1l);
		cardSet.setName("Test");
		cardSet.setAbbreviation("tst");
		cardSet.setLanguage("en");
		
		CardSetService cardSetService = mock(CardSetService.class);
		when(cardSetService.getCardSetById(cardSet.getId())).thenReturn(cardSet);
		
		CardSetIdResource resource = new CardSetIdResource();
		resource.cardSetService = cardSetService;
		
		CardSetModel model = resource.getCardSetById(cardSet.getId());
		
		Assert.assertNotNull("Expected the resource returned by getCardSetById to not be null", model);
	}
	
}
