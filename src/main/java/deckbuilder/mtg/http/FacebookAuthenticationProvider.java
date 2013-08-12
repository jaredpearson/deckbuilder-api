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
    	Map<String, Object> properties = facebookService.getUserInfo(token);
    	String username = properties.get("username").toString();
    	
    	User user = null;
    	try {
    		user = userService.getUserByFacebookUsername(username);
    	} catch(NoResultException exc) {
        	//occurs when the user cannot be found 
    		logger.finest(String.format("Username %s is not found within UserService.", username));
    	}
    	
    	//if we havent seen this user before, we need to go ahead and create a new user account
    	if(user == null) {
    		logger.finest(String.format("Creating new user from with Facebook username %s.", username));
    		user = new User();
    		user.setFacebookUsername(username);
    		userService.createUser(user);
    	}
    	
    	return user;
	}
	
}