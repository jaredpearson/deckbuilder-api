package deckbuilder.mtg.http.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception mapper for {@link NotAuthorizedException}
 * @author jared.pearson
 */
@Provider
public class NotAuthorizedExceptionMapper implements ExceptionMapper<NotAuthorizedException> {
	@Override
	public Response toResponse(NotAuthorizedException exception) {
		return Response
				.status(Status.FORBIDDEN)
				.entity(exception.getEntity())
				.build();
	}
}