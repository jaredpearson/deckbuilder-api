package deckbuilder.mtg.http.rest;

import java.net.URI;

import javax.annotation.Nonnull;
import javax.ws.rs.core.UriInfo;

import deckbuilder.mtg.http.rest.Builder.BuildContext;

public class BuildContextFactory {
	
	public static @Nonnull BuildContext create(@Nonnull final UriInfo uriInfo) {
		assert uriInfo != null;
		return new BuildContext() {
			@Override
			public URI getRequestUri() {
				return uriInfo.getRequestUri();
			}
		};
	}
	
}
