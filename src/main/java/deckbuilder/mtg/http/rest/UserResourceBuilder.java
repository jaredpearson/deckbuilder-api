package deckbuilder.mtg.http.rest;

import javax.annotation.Nonnull;

import deckbuilder.mtg.entities.User;

/**
 * Builds instances of {@link UserResource}
 * @author jared.pearson
 */
public class UserResourceBuilder implements Builder<UserResource> {
	private final EntityUrlFactory urlFactory;
	private final User user;
	
	public UserResourceBuilder(@Nonnull EntityUrlFactory urlFactory, @Nonnull User user) {
		assert urlFactory != null;
		assert user != null;
		this.urlFactory = urlFactory;
		this.user = user;
	}
	
	@Override
	public UserResource build() {
		final String url = urlFactory.createEntityUrl(User.class, user.getId());
		final long id = user.getId();
		return new UserResource(url, id);
	}
}
