package deckbuilder.mtg.service.impl;

import org.junit.Assert;
import org.junit.Test;

import deckbuilder.mtg.db.JpaPersistenceTest;
import deckbuilder.mtg.entities.Card;
import deckbuilder.mtg.entities.CardSet;

public class DefaultCardServiceTest extends JpaPersistenceTest {

	@Test
	public void testGetCardById() throws Exception {
		CardSet cardSet = new CardSet();
		cardSet.setName("Set");
		cardSet.setLanguage("en");
		cardSet.setAbbreviation("set");
		getEntityManager().persist(cardSet);
		
		Card card = new Card();
		card.setName("Test");
		card.setSet(cardSet);
		getEntityManager().persist(card);
		getEntityManager().flush();
		
		DefaultCardService service = new DefaultCardService();
		service.entityManager = getEntityManager();
		
		Card actualCard = service.getCardById(card.getId());
		
		Assert.assertNotNull("Expected getCardById to not return null", actualCard);
		Assert.assertEquals("Expected the card resource to include the ID of the card", card.getId(), actualCard.getId());
		Assert.assertEquals("Expected the card resource to include the name of the card", card.getName(), actualCard.getName());
	}
	
	@Test
	public void testCreateCard() throws Exception {
		CardSet cardSet = new CardSet();
		cardSet.setName("Set");
		cardSet.setLanguage("en");
		cardSet.setAbbreviation("set");
		getEntityManager().persist(cardSet);
		
		Card card = new Card();
		card.setName("Test");
		card.setSet(cardSet);
		
		DefaultCardService service = new DefaultCardService();
		service.entityManager = getEntityManager();
		
		Card actualCard = service.createCard(card);
		Assert.assertNotNull("Expected createCard to create the card", actualCard);
	}
}
