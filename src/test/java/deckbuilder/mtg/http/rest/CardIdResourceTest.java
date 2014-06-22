package deckbuilder.mtg.http.rest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;

import deckbuilder.mtg.entities.Card;
import deckbuilder.mtg.entities.CardSet;
import deckbuilder.mtg.http.rest.models.CardModel;
import deckbuilder.mtg.service.CardService;

public class CardIdResourceTest {

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
		
		CardIdResource resource = new CardIdResource();
		resource.cardService = cardService;
		
		CardModel actualCard = resource.getCardById(card.getId());
		
		Assert.assertNotNull("Expected getCardById to not return null", actualCard);
		Assert.assertEquals("Expected the card resource to include the name of the card", card.getName(), actualCard.getName());
	}
	
}
