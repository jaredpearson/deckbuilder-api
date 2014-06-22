package deckbuilder.mtg.http.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import deckbuilder.mtg.entities.User;
import deckbuilder.mtg.http.rest.Builder.BuildContext;
import deckbuilder.mtg.http.rest.models.UserModel;
import deckbuilder.mtg.http.rest.models.UserModelBuilder;
import deckbuilder.mtg.service.UserService;

@Path("/{version}/user/{id}")
public class UserIdResource {

	@Inject
	EntityUrlFactory urlFactory;
	
	@Inject
	UserService userService;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public UserModel getUserById(@Context UriInfo uriInfo, @PathParam("id") Long id) {
		final User user = userService.getUserById(id);
		final BuildContext context = BuildContextFactory.create(uriInfo);
		return new UserModelBuilder(urlFactory, user).build(context);
	}
	
	/**
	 * Builder for URL instance to {@link UserIdResource}
	 * @author jared.pearson
	 */
	public static class UrlBuilder extends deckbuilder.mtg.http.rest.UrlBuilder {
		private final long id;
		
		public UrlBuilder(long id) {
			this.id = id;
		}
		
		@Override
		protected String buildPath(BuildContext context) {
			return "/v1/user/" + id;
		}
	}
}
