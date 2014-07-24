package deckbuilder.mtg.http;

import java.io.IOException;

import deckbuilder.mtg.entities.User;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.springframework.transaction.annotation.Transactional;

/**
 * Security filter
 */
public class SecurityFilter implements ContainerRequestFilter {
	
	@Context
    private UriInfo uriInfo;
	
	@Inject
	private FacebookAuthenticationProvider facebookAuthenticationProvider;
	
	@Override
	@Transactional(readOnly=false)
	public void filter(ContainerRequestContext requestContext) throws IOException {
		Request request = requestContext.getRequest();
		
		//allow the user to post the user create
    	if(uriInfo.getPath().equals("authUrl") && request.getMethod().equals("GET")) {
    		return;
    	}
    	
    	//allow the OPTIONS to bypass the filter
    	if(request.getMethod().equals("OPTIONS")) {
    		return;
    	}
    	
    	try {
	    	User user = authenticate(requestContext);
	    	requestContext.setSecurityContext(new Authorizer(user));
    	} catch(AuthenticationException exc) {
    		requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized").build());
    	}
	}
	
    private User authenticate(ContainerRequestContext request) throws AuthenticationException {
    	// Extract authentication credentials
        String authentication = request.getHeaders().getFirst("authorization");
        if(authentication == null) {
            throw new AuthenticationException("Authentication credentials are required");
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

        @Override
		public Principal getUserPrincipal() {
            return this.principal;
        }

        @Override
		public boolean isUserInRole(String role) {
        	if("administrator".equalsIgnoreCase(role)) {
        		return user.isAdministrator();
        	}
        	
            return false;
        }

        @Override
		public boolean isSecure() {
            return "https".equals(uriInfo.getRequestUri().getScheme());
        }

        @Override
		public String getAuthenticationScheme() {
            return SecurityContext.BASIC_AUTH;
        }
    }
}