package deckbuilder.mtg.http.config;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import deckbuilder.mtg.http.SecurityFilter;
import deckbuilder.mtg.http.rest.AuthenticationExceptionMapper;
import deckbuilder.mtg.http.rest.BuildContextFactory;
import deckbuilder.mtg.http.rest.CardIdResource;
import deckbuilder.mtg.http.rest.CardResource;
import deckbuilder.mtg.http.rest.CardSetIdCardsResource;
import deckbuilder.mtg.http.rest.CardSetIdResource;
import deckbuilder.mtg.http.rest.CardSetResource;
import deckbuilder.mtg.http.rest.DeckIdCardsIdResource;
import deckbuilder.mtg.http.rest.DeckIdCardsResource;
import deckbuilder.mtg.http.rest.DeckIdResource;
import deckbuilder.mtg.http.rest.DeckResource;
import deckbuilder.mtg.http.rest.EntityUrlFactory;
import deckbuilder.mtg.http.rest.JpaNoResultExceptionMapper;
import deckbuilder.mtg.http.rest.NoResultExceptionMapper;
import deckbuilder.mtg.http.rest.NotAuthorizedExceptionMapper;
import deckbuilder.mtg.http.rest.UserIdResource;
import deckbuilder.mtg.http.rest.UserResource;

/**
 * Configuration for the JAX-RS beans
 * <p>
 * Note: The use SCOPE_SINGLETON is because the Spring-to-Jersey integration
 * uses the annotation value. The default scope (value of "") is unknown to 
 * the Spring-to-Jersey integration and fails.
 * @author jared.pearson
 *
 */
@org.springframework.context.annotation.Configuration
public class JaxRsConfig {

	@Bean
	public EntityUrlFactory entityUrlFactory() {
		return new EntityUrlFactory();
	}
	
	@Bean
	public BuildContextFactory buildContextFactory() {
		return new BuildContextFactory();
	}
	
	//#####################################################
	// FILTERS
	//#####################################################
	
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public SecurityFilter securityFilter() {
		return new SecurityFilter();
	}

	
	//#####################################################
	// EXCEPTION MAPPERS
	//#####################################################
	
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public AuthenticationExceptionMapper authenticationExceptionMapper() {
		return new AuthenticationExceptionMapper();
	}
	
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public JpaNoResultExceptionMapper jpaNoResultExceptionMapper() {
		return new JpaNoResultExceptionMapper();
	}
	
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public NoResultExceptionMapper noResultExceptionMapper() {
		return new NoResultExceptionMapper();
	}
	
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public NotAuthorizedExceptionMapper notAuthorizedExceptionMapper() {
		return new NotAuthorizedExceptionMapper();
	}
	
	//#####################################################
	// RESOURCES
	//#####################################################
	
	@Bean
	public CardResource cardResource() {
		return new CardResource();
	}
	
	@Bean
	public CardIdResource cardIdResource() {
		return new CardIdResource();
	}
	
	@Bean
	public CardSetResource cardSetResource() {
		return new CardSetResource();
	}
	
	@Bean
	public CardSetIdResource cardSetIdResource() {
		return new CardSetIdResource();
	}
	
	@Bean
	public CardSetIdCardsResource cardSetIdCardsResource() {
		return new CardSetIdCardsResource();
	}
	
	@Bean
	public DeckResource deckResource() {
		return new DeckResource();
	}
	
	@Bean
	public DeckIdResource deckIdResource() {
		return new DeckIdResource();
	}
	
	@Bean
	public DeckIdCardsResource deckIdCardsResource() {
		return new DeckIdCardsResource();
	}
	
	@Bean
	public DeckIdCardsIdResource deckIdCardsIdResource() {
		return new DeckIdCardsIdResource();
	}
	
	@Bean
	public UserResource userResource() {
		return new UserResource();
	}
	
	@Bean
	public UserIdResource userIdResource() {
		return new UserIdResource();
	}
}