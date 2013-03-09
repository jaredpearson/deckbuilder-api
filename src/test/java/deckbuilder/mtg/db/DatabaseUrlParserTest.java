package deckbuilder.mtg.db;

import org.junit.Assert;
import org.junit.Test;


public class DatabaseUrlParserTest {

	@Test
	public void testPostgresFullUrl() throws Exception {
		String value = "postgres://testuser:testpassword@abc.host.com:8080/testdb";
		DatabaseCredentials credentials = (new DatabaseUrlParser()).parseDatabaseUrl(value);
		Assert.assertEquals("Unexpected JDBC url", "jdbc:postgresql://abc.host.com:8080/testdb", credentials.getJdbcUrl());
		Assert.assertEquals("Expected username to be testuser", "testuser", credentials.getUsername());
		Assert.assertEquals("Expected password to be testpassword", "testpassword", credentials.getPassword());
		
	}
	
	@Test
	public void testHsqldbFileUrl() throws Exception {
		String value = "hsqldb:file:/opt/db/testdb;ifexists=true";
		DatabaseCredentials credentials = (new DatabaseUrlParser()).parseDatabaseUrl(value);
		Assert.assertEquals("Unexpected JDBC url", "jdbc:hsqldb:file:/opt/db/testdb;ifexists=true", credentials.getJdbcUrl());
		Assert.assertEquals("Expected username to be SA", "SA", credentials.getUsername());
		Assert.assertEquals("Expected password to be blank", "", credentials.getPassword());
	}

}
