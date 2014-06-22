package deckbuilder.mtg.http.rest;

import static org.mockito.Mockito.*;

import javax.ws.rs.core.UriInfo;

import org.junit.Assert;
import org.junit.Test;

import deckbuilder.mtg.entities.Card;
import deckbuilder.mtg.entities.CardSet;
import deckbuilder.mtg.http.rest.Builder.BuildContext;
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
		
		UriInfo uriInfo = mock(UriInfo.class);
		BuildContext buildContext = mock(BuildContext.class);
		BuildContextFactory buildContextFactory = mock(BuildContextFactory.class);
		when(buildContextFactory.create(uriInfo)).thenReturn(buildContext);
		CardService cardService = mock(CardService.class);
		when(cardService.getCardById(card.getId())).thenReturn(card);
		UrlBuilder urlBuilder = mock(UrlBuilder.class);
		when(urlBuilder.build(buildContext)).thenReturn("http://test.com");
		EntityUrlFactory urlFactory = mock(EntityUrlFactory.class);
		when(urlFactory.createEntityUrl(any(Class.class), any())).thenReturn(urlBuilder);
		
		CardIdResource resource = new CardIdResource();
		resource.cardService = cardService;
		resource.buildContextFactory = buildContextFactory;
		resource.urlFactory = urlFactory;
		
		CardModel actualCard = resource.getCardById(uriInfo, card.getId());
		
		Assert.assertNotNull("Expected getCardById to not return null", actualCard);
		Assert.assertEquals("Expected the card resource to include the name of the card", card.getName(), actualCard.getName());
	}
	
}
