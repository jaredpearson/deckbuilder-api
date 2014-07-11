package deckbuilder.mtg.http.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import deckbuilder.mtg.http.AuthenticationException;

/**
 * Map an authentication exception to an HTTP 401 response
 */
@Provider
public class AuthenticationExceptionMapper implements ExceptionMapper<AuthenticationException> {
	@Override
    public Response toResponse(AuthenticationException e) {
        return Response
        		.status(Status.UNAUTHORIZED)
        		.type("text/plain")
        		.entity(e.getMessage())
        		.build();
    }
}
