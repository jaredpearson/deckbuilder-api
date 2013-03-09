package deckbuilder.mtg.service.impl;

import org.junit.Assert;
import org.junit.Test;

import deckbuilder.mtg.db.JpaPersistenceTest;
import deckbuilder.mtg.entities.CardSet;

public class DefaultCardSetServiceTest extends JpaPersistenceTest {

	@Test
	public void testCreateCardSet() throws Exception {
		CardSet cardSet = new CardSet();
		
		DefaultCardSetService service = new DefaultCardSetService();
		service.entityManagerProvider = entityManagerProvider;
		
		CardSet actualCardSet = service.createCardSet(cardSet);
		
		Assert.assertNotNull("Expected createCardSet to never return null", actualCardSet);
	}
	
}
