package deckbuilder.mtg.http.rest;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static deckbuilder.mtg.http.rest.SecurityTestUtils.*;

import java.net.URI;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import deckbuilder.mtg.entities.Card;
import deckbuilder.mtg.entities.CardSet;
import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.entities.DeckCard;
import deckbuilder.mtg.entities.User;
import deckbuilder.mtg.http.rest.Builder.BuildContext;
import deckbuilder.mtg.http.rest.models.DeckCardModel;
import deckbuilder.mtg.service.CardService;
import deckbuilder.mtg.service.DeckCardService;
import deckbuilder.mtg.service.DeckService;

public class DeckIdCardsResourceTest {
	@Test
	public void testCreateDeckCard() throws Exception {
		final User user = new User();
		user.setId(1l);
		
		final Deck deck = new Deck();
		deck.setId(1l);
		deck.setOwner(user);
		final DeckService deckService = mock(DeckService.class);
		when(deckService.getDeckById(deck.getId())).thenReturn(deck);
		
		final Card card = new Card();
		card.setId(1l);
		final CardService cardService = mock(CardService.class);
		when(cardService.getCardById(card.getId())).thenReturn(card);
		
		final CardSet cardSet = new CardSet();
		cardSet.setId(1l);
		card.setSet(cardSet);
		
		final DeckCardService deckCardService = mock(DeckCardService.class);
		when(deckCardService.createDeckCard(any(DeckCard.class))).thenAnswer(new Answer<DeckCard>() {
			@Override
			public DeckCard answer(InvocationOnMock invocation) throws Throwable {
				final DeckCard deckCard = (DeckCard)invocation.getArguments()[0];
				deckCard.setId(1l);
				return deckCard;
			}
		});
		
		final DeckIdCardsResource.DeckCardCreateModel deckCardData = new DeckIdCardsResource.DeckCardCreateModel();
		deckCardData.setCard(1l);
		deckCardData.setDeck(1l);
		deckCardData.setQuantity(1);

		final UriInfo uriInfo = mock(UriInfo.class);
		final URI requestUri = new URI("http://test.com");
		final BuildContext buildContext = mock(BuildContext.class);
		when(buildContext.getRequestUri()).thenReturn(requestUri);
		final BuildContextFactory buildContextFactory = mock(BuildContextFactory.class);
		when(buildContextFactory.create(uriInfo)).thenReturn(buildContext);
		final UrlBuilder urlBuilder = mock(UrlBuilder.class);
		when(urlBuilder.build(buildContext)).thenReturn("http://test.com");
		final EntityUrlFactory urlFactory = mock(EntityUrlFactory.class);
		when(urlFactory.createEntityUrl(any(Class.class), any())).thenReturn(urlBuilder);
		final SecurityContext securityContext = mockSecurityContext(user);
		
		final DeckIdCardsResource controller = new DeckIdCardsResource();
		controller.cardService = cardService;
		controller.deckService = deckService;
		controller.deckCardService = deckCardService;
		controller.buildContextFactory = buildContextFactory;
		controller.urlFactory = urlFactory;
		final Response response = controller.createDeckCard(deckCardData, deck.getId(), uriInfo, securityContext);
		
		Assert.assertNotNull("Expected response to never be null", response);
		final DeckCardModel saveResult = (DeckCardModel)response.getEntity();
		Assert.assertNotNull("Expected createDeckCard to never return null", saveResult);
		Assert.assertNotNull("Expected the save result to have a valid ID", saveResult.getId());
		
		verify(deckCardService).createDeckCard(any(DeckCard.class));
	}
}
