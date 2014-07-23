package deckbuilder.mtg.service.impl;

import org.junit.Assert;
import org.junit.Test;

import deckbuilder.mtg.db.JpaPersistenceTest;
import deckbuilder.mtg.entities.CardSet;

public class DefaultCardSetServiceTest extends JpaPersistenceTest {

	@Test
	public void testCreateCardSet() throws Exception {
		CardSet cardSet = new CardSet();
		cardSet.setName("Set");
		cardSet.setLanguage("en");
		cardSet.setAbbreviation("set");
		
		DefaultCardSetService service = new DefaultCardSetService();
		service.entityManager = getEntityManager();
		
		CardSet actualCardSet = service.createCardSet(cardSet);
		
		Assert.assertNotNull("Expected createCardSet to never return null", actualCardSet);
	}
	
}
