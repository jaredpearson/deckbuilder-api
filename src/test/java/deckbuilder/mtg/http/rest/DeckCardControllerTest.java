package deckbuilder.mtg.http.rest;

import static org.mockito.Mockito.*;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import deckbuilder.mtg.entities.Card;
import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.entities.DeckCard;
import deckbuilder.mtg.entities.User;
import deckbuilder.mtg.http.rest.DeckCardResource.DeckCardCreateModel;
import deckbuilder.mtg.http.rest.DeckCardResource.DeckCardSaveContext;
import deckbuilder.mtg.service.CardService;
import deckbuilder.mtg.service.DeckCardService;
import deckbuilder.mtg.service.DeckService;

public class DeckCardControllerTest {
	@Test
	public void testCreateDeckCard() throws Exception {
		User user = new User();
		user.setId(1l);
		
		Deck deck = new Deck();
		deck.setId(1l);
		deck.setOwner(user);
		DeckService deckService = mock(DeckService.class);
		when(deckService.getDeckById(deck.getId())).thenReturn(deck);
		
		Card card = new Card();
		card.setId(1l);
		CardService cardService = mock(CardService.class);
		when(cardService.getCardById(card.getId())).thenReturn(card);
		
		DeckCardService deckCardService = mock(DeckCardService.class);
		when(deckCardService.createDeckCard(any(DeckCard.class))).thenAnswer(new Answer<DeckCard>() {
			@Override
			public DeckCard answer(InvocationOnMock invocation) throws Throwable {
				DeckCard deckCard = (DeckCard)invocation.getArguments()[0];
				deckCard.setId(1l);
				return deckCard;
			}
		});
		
		DeckCardCreateModel deckCardData = new DeckCardCreateModel();
		deckCardData.setCard(1l);
		deckCardData.setDeck(1l);
		deckCardData.setQuantity(1);
		
		DeckCardResource controller = new DeckCardResource();
		controller.cardService = cardService;
		controller.deckService = deckService;
		controller.deckCardService = deckCardService;
		DeckCardSaveContext saveResult = controller.createDeckCard(deckCardData);
		
		Assert.assertNotNull("Expected createDeckCard to never return null", saveResult);
		Assert.assertNotNull("Expected the save result to have a valid ID", saveResult.getId());
		
		verify(deckCardService).createDeckCard(any(DeckCard.class));
	}
}
