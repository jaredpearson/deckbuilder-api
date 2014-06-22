package deckbuilder.mtg.http.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import deckbuilder.mtg.entities.User;
import deckbuilder.mtg.http.rest.models.UserModel;
import deckbuilder.mtg.http.rest.models.UserModelBuilder;
import deckbuilder.mtg.service.UserService;

/**
 * Controller for dealing with /user paths
 * @author jared.pearson
 */
@Path("/{version}/user")
public class UserResource {
	
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
}