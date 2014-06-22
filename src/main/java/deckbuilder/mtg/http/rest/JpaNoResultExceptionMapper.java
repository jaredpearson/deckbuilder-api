package deckbuilder.mtg.http.rest;

import javax.persistence.NoResultException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception mapper for {@link NoResultException}
 * @author jared.pearson
 */
@Provider
public class JpaNoResultExceptionMapper implements ExceptionMapper<NoResultException> {
	@Override
	public Response toResponse(NoResultException exception) {
		return Response
				.status(Status.NOT_FOUND)
				.build();
	}
}
