package deckbuilder.mtg.service.impl;

import org.junit.Assert;
import org.junit.Test;

import deckbuilder.mtg.db.JpaPersistenceTest;
import deckbuilder.mtg.entities.Card;
import deckbuilder.mtg.entities.CardSet;
import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.entities.DeckCard;
import deckbuilder.mtg.entities.User;

public class DefaultDeckCardServiceTest extends JpaPersistenceTest {
	@Test
	public void testCreateDeckCard() throws Exception {
		User user = new User();
		entityManager.persist(user);
		
		Deck deck = new Deck();
		deck.setOwner(user);
		entityManager.persist(deck);
		
		CardSet cardSet = new CardSet();
		cardSet.setName("Set");
		cardSet.setAbbreviation("set");
		cardSet.setLanguage("en");
		entityManager.persist(cardSet);
		
		Card card = new Card();
		card.setSet(cardSet);
		entityManager.persist(card);
		entityManager.flush();
		
		DeckCard deckCardData = new DeckCard();
		deckCardData.setCard(card);
		deckCardData.setDeck(deck);
		deckCardData.setQuantity(1);
		
		DefaultDeckCardService service = new DefaultDeckCardService();
		service.entityManagerProvider = entityManagerProvider;
		DeckCard saveResult = service.createDeckCard(deckCardData);
		
		Assert.assertNotNull("Expected createDeckCard to never return null", saveResult);
		Assert.assertNotNull("Expected the save result to have a valid ID", saveResult.getId());
		
		DeckCard deckCard = entityManager.find(DeckCard.class, saveResult.getId());
		Assert.assertNotNull("Expected create deck card to save", deckCard);
	}
}
