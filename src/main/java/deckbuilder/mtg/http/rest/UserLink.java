package deckbuilder.mtg.http.rest;

import deckbuilder.mtg.entities.User;

public class UserLink extends Link {
	private static final long serialVersionUID = 1L;

	public UserLink(User user) {
		this.put("id", user.getId());
	}
}
