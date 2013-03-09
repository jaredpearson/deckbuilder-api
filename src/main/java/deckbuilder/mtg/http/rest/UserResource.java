package deckbuilder.mtg.http.rest;

import deckbuilder.mtg.entities.User;

public class UserResource extends Resource {
	private static final long serialVersionUID = 1L;

	public UserResource(User user) {
		put("id", user.getId());
	}
}