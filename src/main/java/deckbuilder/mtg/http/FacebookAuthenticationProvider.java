package deckbuilder.mtg.http;

import java.util.Map;

import javax.inject.Inject;

import deckbuilder.mtg.entities.User;
import deckbuilder.mtg.facebook.FacebookService;
import deckbuilder.mtg.service.NoResultException;
import deckbuilder.mtg.service.UserService;

public class FacebookAuthenticationProvider implements AuthenticationProvider {
	@Inject
	FacebookService facebookService;
	
	@Inject
	UserService userService;
	
	@Override
	public User authenticate(String token) throws AuthenticationException {
    	Map<String, Object> properties = facebookService.getUserInfo(token);
    	String username = properties.get("username").toString();
    	
    	User user = null;
    	try {
    		user = userService.getUserByFacebookUsername(username);
    	} catch(NoResultException exc) {
        	//occurs when the user cannot be found 
    	}
    	
    	//if we havent seen this user before, we need to go ahead and create a new user account
    	if(user == null) {
    		user = new User();
    		user.setFacebookUsername(username);
    		userService.createUser(user);
    	}
    	
    	return user;
	}
}