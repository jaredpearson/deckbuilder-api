package deckbuilder.mtg.db;

import java.net.URI;
import java.net.URISyntaxException;

public class DatabaseUrlParser {
	
	/**
	 * Given a database string with the following formats.
	 * <p>
	 * Postgres:<br>
	 * <code>postgres://[username]:[password]@[host]:[port]</code>
	 * <p>
	 * Hsqldb:<br>
	 * <code>hsqldb:hsqls://[host]/[database]</code><br>
	 * <code>hsqldb:https://[host]/[database]</code>
	 * <code>hsqldb:file:[file path]</code>
	 */
	public DatabaseCredentials parseDatabaseUrl(String value) {
		if(value == null) {
			return null;
		}
		
		if(value.startsWith("postgres:")) {
			try {
				URI uri = new URI(value);
				return createPostgresfromUri(uri);
			} catch (URISyntaxException exc) {
				throw new RuntimeException(exc);
			}
		} else if(value.startsWith("hsqldb:")) {
			return new DatabaseCredentials("jdbc:" + value, "SA", "", "org.hibernate.dialect.HSQLDialect", "org.hsqldb.jdbcDriver");
		} else {
			throw new RuntimeException("Syntax for specified value is unknown: " + value);
		}
	}
	
	private static DatabaseCredentials createPostgresfromUri(URI uri) {
		//parse the username and password
		String username = null;
		String password = null;
		String userinfo = uri.getUserInfo();
		if(userinfo != null) {
			String[] parts = userinfo.split(":");
			username = parts[0];
			password = parts[1];
		}
		
		String jdbcUri = "jdbc:postgresql://" + uri.getHost() + ":" + uri.getPort() + uri.getPath();
		return new DatabaseCredentials(jdbcUri, username, password, "org.hibernate.dialect.PostgreSQLDialect", "org.postgresql.Driver");
	}
}
