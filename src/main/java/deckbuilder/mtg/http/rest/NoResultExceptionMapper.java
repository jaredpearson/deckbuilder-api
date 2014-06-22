package deckbuilder.mtg.http.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import deckbuilder.mtg.service.NoResultException;

/**
 * Exception Mapper for the {@link NoResultException}
 * @author jared.pearson
 */
@Provider
public class NoResultExceptionMapper implements ExceptionMapper<NoResultException> {
	@Override
	public Response toResponse(NoResultException exception) {
		return Response
				.status(Status.NOT_FOUND)
				.build();
	}
}
