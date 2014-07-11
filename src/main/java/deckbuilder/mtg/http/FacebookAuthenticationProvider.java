package deckbuilder.mtg.http;

import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;

import deckbuilder.mtg.entities.User;
import deckbuilder.mtg.facebook.FacebookService;
import deckbuilder.mtg.service.NoResultException;
import deckbuilder.mtg.service.UserService;

public class FacebookAuthenticationProvider implements AuthenticationProvider {
	private static final Logger logger = Logger.getLogger(FacebookAuthenticationProvider.class.getName());
	
	@Inject
	private FacebookService facebookService;
	
	@Inject
	private UserService userService;
	
	@Override
	public User authenticate(final String token) throws AuthenticationException {
    	final Map<String, Object> properties = facebookService.getUserInfo(token);
    	final Long facebookId = (Long)properties.get("id");
    	if (facebookId == null) {
    		throw new IllegalStateException("No ID returned from getting the user information from Facebook.");
    	}
    	
    	User user = null;
    	try {
    		user = userService.getUserByFacebookId(facebookId);
    	} catch(NoResultException exc) {
        	//occurs when the user cannot be found 
    		logger.finest(String.format("Facebook ID %s is not found within UserService.", facebookId));
    	}
    	
    	//if we havent seen this user before, we need to go ahead and create a new user account
    	if(user == null) {
    		logger.finest(String.format("Creating new user from with Facebook ID %s.", facebookId));
    		user = new User();
    		user.setFacebookId(facebookId);
    		userService.createUser(user);
    	}
    	
    	return user;
	}
	
}