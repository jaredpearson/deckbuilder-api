package deckbuilder.mtg.http.rest;

import javax.annotation.Nonnull;

import deckbuilder.mtg.entities.User;

/**
 * Builds instances of {@link UserModel}
 * @author jared.pearson
 */
public class UserModelBuilder implements Builder<UserModel> {
	private final EntityUrlFactory urlFactory;
	private final User user;
	
	public UserModelBuilder(@Nonnull EntityUrlFactory urlFactory, @Nonnull User user) {
		assert urlFactory != null;
		assert user != null;
		this.urlFactory = urlFactory;
		this.user = user;
	}
	
	@Override
	public UserModel build() {
		final String url = urlFactory.createEntityUrl(User.class, user.getId());
		final long id = user.getId();
		return new UserModel(url, id);
	}
}
