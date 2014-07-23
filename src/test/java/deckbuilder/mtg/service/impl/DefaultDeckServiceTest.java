package deckbuilder.mtg.service.impl;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import deckbuilder.mtg.db.JpaPersistenceTest;
import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.entities.User;

public class DefaultDeckServiceTest extends JpaPersistenceTest{
	@Test
	public void testList() throws Exception {
		User user = createTestUser();
		
		Deck deck = new Deck();
		deck.setOwner(user);
		getEntityManager().persist(deck);
		getEntityManager().flush();

		DefaultDeckService service = new DefaultDeckService();
		service.entityManager = getEntityManager();
		List<Deck> decks = service.getDecksForOwner(user.getId());
		
		Assert.assertEquals("Expected list to return all available decks", 1, decks.size());
		Assert.assertEquals("Expected the resource returned to be the one in returned by the service.", deck.getId(), decks.get(0).getId());
	}
	
	@Test
	public void testCreateDeck() throws Exception {
		User user = createTestUser();
		getEntityManager().flush();

		Deck deckData = new Deck();
		deckData.setName("Test");
		deckData.setOwner(user);
		
		DefaultDeckService service = new DefaultDeckService();
		service.entityManager = getEntityManager();
		Deck saveResponse = service.createDeck(deckData);
		
		Assert.assertNotNull("Expected createDeck to return a save response.", saveResponse);
		
		Long newDeckId = saveResponse.getId();
		Assert.assertNotNull("Expected the save response to return the deck ID", newDeckId);
		
		Deck deck = getEntityManager().find(Deck.class, newDeckId);
		Assert.assertNotNull("Expected the createDeck method to create a deck", deck);
	}
	
	@Test
	public void testDeleteDeck() throws Exception {
		User owner = createTestUser();
		
		Deck deck = new Deck();
		deck.setOwner(owner);
		getEntityManager().persist(deck);
		getEntityManager().flush();

		DefaultDeckService service = new DefaultDeckService();
		service.entityManager = getEntityManager();
		service.deleteDeck(deck.getId());
		
		getEntityManager().flush();
		Deck actual = getEntityManager().find(Deck.class, deck.getId());
		Assert.assertNull("Expected the deck to be deleted", actual);
	}
	
	@Test
	public void testUpdateDeck() throws Exception {
		User owner = createTestUser();
		
		Deck deck = new Deck();
		deck.setOwner(owner);
		getEntityManager().persist(deck);
		getEntityManager().flush();
		
		//change the deck name
		deck.setName("New Name!");

		DefaultDeckService service = new DefaultDeckService();
		service.entityManager = getEntityManager();
		service.updateDeck(deck);
		
		Deck actualDeck = getEntityManager().find(Deck.class, deck.getId());
		Assert.assertNotNull("Expected the deck to be found with the ID", actualDeck);
		Assert.assertEquals("Expected the name of the deck to have changed after the update", deck.getName(), actualDeck.getName());
	}
	
	private User createTestUser() {
		User owner = new User();
		getEntityManager().persist(owner);
		return owner;
	}
	
}
