package deckbuilder.mtg.http;

import deckbuilder.mtg.entities.User;

public class Principal implements java.security.Principal {
	private final User user;

	public Principal(final User user) {
		this.user = user;
	}
	
	public User getUser() {
		return this.user;
	}
	
	@Override
	public String getName() {
	    return user.getFacebookUsername();
	}
}