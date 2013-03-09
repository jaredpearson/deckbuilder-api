package deckbuilder.mtg.service.impl;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.google.inject.Provider;

import deckbuilder.mtg.Configuration;
import deckbuilder.mtg.entities.User;
import deckbuilder.mtg.service.NoResultException;
import deckbuilder.mtg.service.UserService;

public class DefaultUserService implements UserService {
	
	@Inject
	Provider<EntityManager> entityManagerProvider;
	
	@Inject
	Configuration configuration;
	
	@Override
	public User getUserById(Long id) {
		User user = entityManagerProvider.get().find(User.class, id);
		if(user == null) {
			throw new NoResultException();
		}
		return user;
	}
	
	@Override
	public User createUser(User user) {
		if(isAdministrator(user)) {
			user.setAdministrator(true);
		}
		
		entityManagerProvider.get().persist(user);
		return user;
	}
	
	@Override
	public User updateUser(User user) {
		entityManagerProvider.get().merge(user);
		return user;
	}
	
	@Override
	public User getUserByFacebookUsername(String username) {
		try {
			EntityManager entityManager = entityManagerProvider.get();
			TypedQuery<User> query = entityManager.createQuery("select u from User u where lower(facebookUsername) = :username", User.class);
			return query.setParameter("username", toLowercase(username)).getSingleResult();
		} catch(javax.persistence.NoResultException exc) {
			throw new NoResultException(exc);
		}
	}
	
	private boolean isAdministrator(User user) {
		if(user == null) {
			return false;
		}
		if(configuration == null || configuration.getAdministratorNames() == null) {
			return false;
		}
		
		for(String adminName : configuration.getAdministratorNames()) {
			if(adminName != null && adminName.equalsIgnoreCase(user.getFacebookUsername())) {
				return true;
			}
		}
		return false;
	}
	
	private static String toLowercase(String value) {
		if(value == null) {
			return null;
		}
		return value.toLowerCase();
	}
}
