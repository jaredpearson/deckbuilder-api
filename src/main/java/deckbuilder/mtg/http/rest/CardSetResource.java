package deckbuilder.mtg.http.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import deckbuilder.mtg.entities.CardSet;
import deckbuilder.mtg.http.rest.Builder.BuildContext;
import deckbuilder.mtg.http.rest.models.CardSetListModel;
import deckbuilder.mtg.http.rest.models.CardSetModel;
import deckbuilder.mtg.http.rest.models.CardSetModelBuilder;
import deckbuilder.mtg.service.CardSetService;

@Path("/{version}/set")
@Produces(MediaType.APPLICATION_JSON)
@Transactional(readOnly=true)
public class CardSetResource {
	
	@Inject
	EntityUrlFactory urlFactory;
	
	@Inject
	CardSetService cardSetService;
	
	@Inject
	BuildContextFactory buildContextFactory;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public CardSetListModel list(@Context UriInfo uriInfo) throws Exception {
		final List<CardSet> sets = cardSetService.getCardSets();
		final BuildContext context = buildContextFactory.create(uriInfo);
		final List<CardSetModel> resources = Lists.newArrayListWithExpectedSize(sets.size());
		for (CardSet cardSet : sets) {
			final CardSetModel resource = new CardSetModelBuilder(urlFactory, cardSet).build(context);
			assert resource != null;
			resources.add(resource);
		}
		return new CardSetListModel(resources);
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly=false)
	public Response createCardSet(CardSetCreateModel cardSetData, @Context SecurityContext securityContext) throws Exception {
		
		//only administrators can create card sets
		if(!securityContext.isUserInRole("administrator")) {
			return Response.status(Status.FORBIDDEN).build();
		}
		
		// validate the properties
		validateText("name", cardSetData.getName(), false, 30);
		validateText("abbreviation", cardSetData.getAbbreviation(), false, 5);
		validateText("language", cardSetData.getLanguage(), false, 2);
		
		CardSet cardSet = new CardSet();
		cardSet.setName(Strings.nullToEmpty(cardSetData.getName()).trim());
		cardSet.setAbbreviation(Strings.nullToEmpty(cardSetData.getAbbreviation()).trim());
		cardSet.setLanguage(Strings.nullToEmpty(cardSetData.getLanguage()).trim().toLowerCase());
		
		cardSet = cardSetService.createCardSet(cardSet);
		
		return Response.ok(new CardSetSaveResponse(cardSet.getId())).build();
	}
	
	private void validateText(String name, String value, boolean nullAllowed, int maxLength) {
		if (nullAllowed && value == null) {
			return;
		}
		
		final String cleanValue = Strings.nullToEmpty(value).trim();
		if (cleanValue.isEmpty()) {
			throw new IllegalArgumentException("Expected " + name + " to be specified");
		}
		if (cleanValue.length() > maxLength) {
			throw new IllegalArgumentException(name + " must be less than 5 characters");
		}
	}
	
	/**
	 * Response for when a {@link CardSet} is created
	 * @author jared.pearson
	 */
	public static class CardSetSaveResponse extends SaveResponse {
		private Long id;
		
		public CardSetSaveResponse(Long id) {
			super(true, null);
			this.id = id;
		}
		
		public Long getId() {
			return id;
		}
	}
	
	/**
	 * Model used to create {@link CardSet} instances
	 * @author jared.pearson
	 */
	public static class CardSetCreateModel {
		private String name;
		private String abbreviation;
		private String language;
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public void setLanguage(String language) {
			this.language = language;
		}
		
		public String getLanguage() {
			return language;
		}
		
		public String getAbbreviation() {
			return abbreviation;
		}
		
		public void setAbbreviation(String abbreviation) {
			this.abbreviation = abbreviation;
		}
	}
}
