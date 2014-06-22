package deckbuilder.mtg.http.rest;

import java.net.URI;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.UriInfo;

import deckbuilder.mtg.Version;
import deckbuilder.mtg.http.rest.Builder.BuildContext;

public class BuildContextFactory {
	@Inject
	@Named("currentVersion")
	Version currentVersion; 
	
	public @Nonnull BuildContext create(@Nonnull final UriInfo uriInfo) {
		assert uriInfo != null;
		return new BuildContext() {
			@Override
			public URI getRequestUri() {
				return uriInfo.getRequestUri();
			}
			
			@Override
			public Version getVersion() {
				return BuildContextFactory.this.currentVersion;
			}
		};
	}
	
}
