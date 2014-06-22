package deckbuilder.mtg.http.rest;

import java.net.URI;

import javax.annotation.Nonnull;

public abstract class UrlBuilder implements Builder<String> {
	
	@Override
	public String build(@Nonnull BuildContext context) {
		assert context != null;
		final URI requestUri = context.getRequestUri();
		final String baseUrl = requestUri.getScheme() + "://" + requestUri.getHost() + (requestUri.getPort() != -1 ? ":" + requestUri.getPort() : "");
		return baseUrl + buildPath(context);
	}
	
	protected abstract String buildPath(@Nonnull BuildContext context);
}
