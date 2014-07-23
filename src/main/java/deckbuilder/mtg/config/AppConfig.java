package deckbuilder.mtg.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import deckbuilder.mtg.Configuration;
import deckbuilder.mtg.Version;
import deckbuilder.mtg.Configuration.CorsConfiguration;
import deckbuilder.mtg.service.CardService;
import deckbuilder.mtg.service.CardSetService;
import deckbuilder.mtg.service.DeckCardService;
import deckbuilder.mtg.service.DeckService;
import deckbuilder.mtg.service.UserService;
import deckbuilder.mtg.service.impl.DefaultCardService;
import deckbuilder.mtg.service.impl.DefaultCardSetService;
import deckbuilder.mtg.service.impl.DefaultDeckCardService;
import deckbuilder.mtg.service.impl.DefaultDeckService;
import deckbuilder.mtg.service.impl.DefaultUserService;

/**
 * Configuration for the general application services and settings
 * <p>
 * Note: The use SCOPE_SINGLETON is because the Spring-to-Jersey integration
 * uses the annotation value. The default scope (value of "") is unknown to 
 * the Spring-to-Jersey integration and fails.
 * @author jared.pearson
 */
@org.springframework.context.annotation.Configuration
public class AppConfig {

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public UserService userService() {
		return new DefaultUserService();
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public CardService cardService() {
		return new DefaultCardService();
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public DeckService deckService() {
		return new DefaultDeckService();
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public DeckCardService deckCardService() {
		return new DefaultDeckCardService();
	}
	
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public CardSetService cardSetService() {
		return new DefaultCardSetService();
	}
	
	@Bean(name = "currentVersion")
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public Version currentVersion() {
		return new Version("v1");
	}
	
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public Configuration config() {
		//load the properties from the standard location
		Properties properties = new Properties();
		try(InputStream inputStream = getClass().getResourceAsStream("/deckbuilder/mtg/config.properties")) {
			properties.load(inputStream);
		} catch(IOException exc) {
			throw new RuntimeException(exc);
		}
		

		//get the administrators
		final List<String> administratorNames;
		final String adminNamesAsString = properties.getProperty("administrators");
		if(adminNamesAsString != null) {
			administratorNames = Arrays.asList(adminNamesAsString.split(","));
		} else {
			administratorNames = Collections.emptyList();
		}
		
		// get the cors.* properties
		final String corsAllowedOrigins = properties.getProperty("cors.allowedOrigins", ""); 
		CorsConfiguration corsConfig = new CorsConfiguration(corsAllowedOrigins);
		
		return new Configuration(administratorNames, corsConfig);
	}
}