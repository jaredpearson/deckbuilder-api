package deckbuilder.mtg.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hsqldb.jdbc.JDBCDataSourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import deckbuilder.mtg.db.DatabaseCredentials;
import deckbuilder.mtg.db.DatabaseService;
import deckbuilder.mtg.db.DatabaseUrlParser;

/**
 * Configuration for the database beans
 * @author jared.pearson
 */
@Configuration
@EnableTransactionManagement
public class DbConfig {
	
	@Bean
	public DatabaseCredentials dbCreds() {
		//the database url is given in following format
		// [type]://[username]:[password]@[host]:[port]
		final String databaseUrl = System.getenv("DATABASE_URL");
		if(databaseUrl == null) {
			throw new RuntimeException("DATABASE_URL environment variable not found.");
		}
		return (new DatabaseUrlParser()).parseDatabaseUrl(databaseUrl);
	}
	
	@Bean
	public DataSource dataSource() {
		final DatabaseCredentials credentials = dbCreds();
		try {
			final Properties dbProps = new Properties();
			dbProps.setProperty("url", credentials.getJdbcUrl());
			dbProps.setProperty("user", credentials.getUsername());
			dbProps.setProperty("password", credentials.getPassword());
			return JDBCDataSourceFactory.createDataSource(dbProps);
		} catch(Exception exc) {
			throw new RuntimeException(exc);
		}
	}
	
	@Bean
	@Lazy
	public DatabaseService databaseService() {
		return new DatabaseService(dataSource());
	}
	
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		final DatabaseCredentials dbCreds = dbCreds();
		
		final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setGenerateDdl(false);
		vendorAdapter.setShowSql(true);
		vendorAdapter.setDatabasePlatform(dbCreds.getHibernateDialect());
		
		final Properties properties = new Properties();
	    properties.put("javax.persistence.jdbc.driver", dbCreds.getJdbcDriverClassName());
	    properties.put("javax.persistence.jdbc.url", dbCreds.getJdbcUrl());
	    properties.put("javax.persistence.jdbc.user", dbCreds.getUsername());
	    properties.put("javax.persistence.jdbc.password", dbCreds.getPassword());
		
	    final DataSource dataSource = dataSource();
		final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPackagesToScan("deckbuilder.mtg");
		factory.setPersistenceUnitName("deckbuilder.mtg");
		factory.setDataSource(dataSource);
		factory.setJpaProperties(properties);
		
		return factory;
	}
	
	@Bean
	public PlatformTransactionManager txManager(EntityManagerFactory emf) {
		final JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);
		return transactionManager;
	}
}