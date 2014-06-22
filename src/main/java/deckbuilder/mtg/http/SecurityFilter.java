package deckbuilder.mtg.http;

import com.google.inject.persist.Transactional;
import com.sun.jersey.api.container.MappableContainerException;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

import deckbuilder.mtg.entities.User;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

/**
 * Security filter
 */
public class SecurityFilter implements ContainerRequestFilter {
	
	@Context
    private UriInfo uriInfo;
	
	@Inject
	private FacebookAuthenticationProvider facebookAuthenticationProvider;
	
	@Transactional
    public ContainerRequest filter(ContainerRequest request) {
    	//allow the user to post the user create
    	if(uriInfo.getPath().equals("authUrl") && request.getMethod().equals("GET")) {
    		return request;
    	}
    	
    	//allow the OPTIONS to bypass the filter
    	if(request.getMethod().equals("OPTIONS")) {
    		return request;
    	}
    	
    	try {
	    	User user = authenticate(request);
	        request.setSecurityContext(new Authorizer(user));
	        return request;
    	} catch(AuthenticationException exc) {
    		throw new MappableContainerException(exc);
    	}
    }

    private User authenticate(ContainerRequest request) throws AuthenticationException {
    	// Extract authentication credentials
        String authentication = request.getHeaderValue(ContainerRequest.AUTHORIZATION);
        if(authentication == null) {
            throw new MappableContainerException(new AuthenticationException("Authentication credentials are required"));
        }
        
        User user = null;
        if(authentication.startsWith("Facebook-Access-Token ")) {
        	authentication = authentication.substring("Facebook-Access-Token ".length());
        	
        	user = facebookAuthenticationProvider.authenticate(authentication);
        	
        } else {
        	throw new IllegalArgumentException("Unsupported authorization type specified");
        }
        
        //If we still don't have a user, then the authentication failed
        if(user == null) {
        	throw new AuthenticationException();
        }
        
        return user;
    }
    
    public class Authorizer implements SecurityContext {
    	private User user;
        private Principal principal;

        public Authorizer(final User user) {
        	this.user = user;
            this.principal = new Principal(user);
        }

        public Principal getUserPrincipal() {
            return this.principal;
        }

        public boolean isUserInRole(String role) {
        	if("administrator".equalsIgnoreCase(role)) {
        		return user.isAdministrator();
        	}
        	
            return false;
        }

        public boolean isSecure() {
            return "https".equals(uriInfo.getRequestUri().getScheme());
        }

        public String getAuthenticationScheme() {
            return SecurityContext.BASIC_AUTH;
        }
    }
}