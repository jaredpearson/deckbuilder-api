package deckbuilder.mtg.http.rest;

import deckbuilder.mtg.entities.User;

public class UserTestUtils {

	private UserTestUtils() {
	}
	
	public static User createTestUser() {
		User owner = new User();
		owner.setId(1l);
		return owner;
	}
}
