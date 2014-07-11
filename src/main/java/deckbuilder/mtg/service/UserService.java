package deckbuilder.mtg.service;

import javax.annotation.Nonnull;

import deckbuilder.mtg.entities.User;

public interface UserService {
	public User createUser(@Nonnull User user);
	public User updateUser(@Nonnull User user);
	
	/**
	 * Gets the user account associated to the given ID. If the account 
	 * cannot be found, a {@link NoResultException} is thrown.
	 * @param id The ID of the user account.
	 * @throws NoResultException when no account is found with the specified ID
	 */
	public @Nonnull User getUserById(@Nonnull Long id) throws NoResultException;
	
	/**
	 * Gets the user account associated to the given Facebook account. If the account 
	 * cannot be found, a {@link NoResultException} is thrown.
	 * @param username The username of the Facebook account.
	 * @throws NoResultException when no account is found with the specified username
	 */
	public @Nonnull User getUserByFacebookUsername(@Nonnull String username) throws NoResultException;
	
	/**
	 * Gets the user account associated to the given Facebook account. If the account 
	 * cannot be found, a {@link NoResultException} is thrown.
	 * @param facebookId The ID of the Facebook account.
	 * @throws NoResultException when no account is found with the specified Facebook ID
	 */
	public @Nonnull User getUserByFacebookId(@Nonnull Long facebookId) throws NoResultException;
}
