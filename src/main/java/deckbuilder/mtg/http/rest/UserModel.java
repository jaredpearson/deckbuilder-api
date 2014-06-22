package deckbuilder.mtg.http.rest;

import java.io.Serializable;

import javax.annotation.Nonnull;

public class UserModel implements Serializable {
	private static final long serialVersionUID = -4416135079669226127L;
	private final String url;
	private final long id;
	
	public UserModel(@Nonnull String url, long id) {
		assert url != null;
		this.url = url;
		this.id = id;
	}
	
	/**
	 * Gets the URL of this
	 */
	public String getUrl() {
		return url;
	}
	
	public long getId() {
		return id;
	}
}