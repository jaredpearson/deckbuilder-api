package deckbuilder.mtg.http.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import deckbuilder.mtg.entities.User;
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
	@Path("/{id}")
	public UserModel getUserById(@PathParam("id") Long id) {
		final User user = userService.getUserById(id);
		return new UserModelBuilder(urlFactory, user).build();
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
		public String build() {
			return "/v1/user/" + id;
		}
	}
}
