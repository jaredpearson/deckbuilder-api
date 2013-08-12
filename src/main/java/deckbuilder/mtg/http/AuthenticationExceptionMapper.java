package deckbuilder.mtg.http;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Map an authentication exception to an HTTP 401 response
 */
@Provider
class AuthenticationExceptionMapper implements ExceptionMapper<AuthenticationException> {
    public Response toResponse(AuthenticationException e) {
        return Response.
                status(Status.UNAUTHORIZED).
                type("text/plain").
                entity(e.getMessage()).
                build();
    }
}
