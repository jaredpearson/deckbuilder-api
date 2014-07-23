package deckbuilder.mtg.service.impl;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.transaction.annotation.Transactional;

import deckbuilder.mtg.Configuration;
import deckbuilder.mtg.entities.User;
import deckbuilder.mtg.service.NoResultException;
import deckbuilder.mtg.service.UserService;

@Transactional(readOnly=true)
public class DefaultUserService implements UserService {

	@PersistenceContext(unitName="deckbuilder.mtg")
	EntityManager entityManager;
	
	@Inject
	Configuration configuration;
	
	@Override
	public User getUserById(Long id) {
		User user = entityManager.find(User.class, id);
		if(user == null) {
			throw new NoResultException();
		}
		return user;
	}
	
	@Override
	@Transactional(readOnly=false)
	public User createUser(User user) {
		if(isAdministrator(user)) {
			user.setAdministrator(true);
		}
		
		entityManager.persist(user);
		return user;
	}
	
	@Override
	@Transactional(readOnly=false)
	public User updateUser(User user) {
		entityManager.merge(user);
		return user;
	}
	
	@Override
	public User getUserByFacebookUsername(String username) {
		try {
			TypedQuery<User> query = entityManager.createQuery("select u from User u where lower(facebookUsername) = :username", User.class);
			return query.setParameter("username", toLowercase(username)).getSingleResult();
		} catch(javax.persistence.NoResultException exc) {
			throw new NoResultException(exc);
		}
	}
	
	@Override
	public User getUserByFacebookId(Long facebookId) throws NoResultException {
		assert facebookId != null;
		try {
			TypedQuery<User> query = entityManager.createQuery("select u from User u where facebookId = :facebookId", User.class);
			return query.setParameter("facebookId", facebookId).getSingleResult();
		} catch(javax.persistence.NoResultException exc) {
			throw new NoResultException(exc);
		}
	}
	
	private boolean isAdministrator(User user) {
		if(user == null) {
			return false;
		}
		if(configuration == null || configuration.administratorNames == null) {
			return false;
		}
		
		for(String adminName : configuration.administratorNames) {
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
