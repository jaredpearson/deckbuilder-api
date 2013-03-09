package deckbuilder.mtg.service;

import deckbuilder.mtg.entities.User;

public interface UserService {
	public User createUser(User user);
	public User updateUser(User user);
	
	/**
	 * Gets the user account associated to the given ID. If the account 
	 * cannot be found, a {@link NoResultException} is thrown.
	 * @param id The ID of the user account.
	 * @throws NoResultException when no account is found with the specified ID
	 */
	public User getUserById(Long id) throws NoResultException;
	
	/**
	 * Gets the user account associated to the given Facebook account. If the account 
	 * cannot be found, a {@link NoResultException} is thrown.
	 * @param username The username of the Facebook account.
	 * @throws NoResultException when no account is found with the specified username
	 */
	public User getUserByFacebookUsername(String username) throws NoResultException;
}
