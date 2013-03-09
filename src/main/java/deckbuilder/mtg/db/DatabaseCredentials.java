package deckbuilder.mtg.db;

public class DatabaseCredentials {
	private String hibernateDialect;
	private String jdbcUrl;
	private String username;
	private String password;
	private String jdbcDriverClassName;
	
	public DatabaseCredentials(String jdbcUrl, String username, String password, String hibernateDialect, String jdbcDriverClassName) {
		this.jdbcUrl = jdbcUrl;
		this.username = username;
		this.password = password;
		this.hibernateDialect = hibernateDialect;
		this.jdbcDriverClassName = jdbcDriverClassName;
	}
	
	public String getJdbcUrl() {
		return this.jdbcUrl;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public String getHibernateDialect() {
		return this.hibernateDialect;
	}
	
	public String getJdbcDriverClassName() {
		return jdbcDriverClassName;
	}
}