package deckbuilder.mtg.http.rest;

import static org.mockito.Mockito.*;

import java.net.URI;

import javax.ws.rs.core.UriInfo;

import org.junit.Assert;
import org.junit.Test;

import deckbuilder.mtg.entities.CardSet;
import deckbuilder.mtg.http.rest.Builder.BuildContext;
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

		UriInfo uriInfo = mock(UriInfo.class);
		URI requestUri = new URI("http://test.com");
		BuildContext buildContext = mock(BuildContext.class);
		when(buildContext.getRequestUri()).thenReturn(requestUri);
		BuildContextFactory buildContextFactory = mock(BuildContextFactory.class);
		when(buildContextFactory.create(uriInfo)).thenReturn(buildContext);
		UrlBuilder urlBuilder = mock(UrlBuilder.class);
		when(urlBuilder.build(buildContext)).thenReturn("http://test.com");
		EntityUrlFactory urlFactory = mock(EntityUrlFactory.class);
		when(urlFactory.createEntityUrl(any(Class.class), any())).thenReturn(urlBuilder);
		
		CardSetService cardSetService = mock(CardSetService.class);
		when(cardSetService.getCardSetById(cardSet.getId())).thenReturn(cardSet);
		
		CardSetIdResource resource = new CardSetIdResource();
		resource.cardSetService = cardSetService;
		resource.buildContextFactory = buildContextFactory;
		resource.urlFactory = urlFactory;
		
		CardSetModel model = resource.getCardSetById(uriInfo, cardSet.getId());
		
		Assert.assertNotNull("Expected the resource returned by getCardSetById to not be null", model);
	}
	
}
