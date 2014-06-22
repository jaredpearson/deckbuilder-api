package deckbuilder.mtg.http;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.persist.PersistFilter;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

import deckbuilder.mtg.Configuration;
import deckbuilder.mtg.facebook.FacebookService;
import deckbuilder.mtg.http.rest.CardIdResource;
import deckbuilder.mtg.http.rest.CardResource;
import deckbuilder.mtg.http.rest.CardSetIdCardsResource;
import deckbuilder.mtg.http.rest.CardSetIdResource;
import deckbuilder.mtg.http.rest.CardSetResource;
import deckbuilder.mtg.http.rest.DeckCardResource;
import deckbuilder.mtg.http.rest.DeckIdCardsResource;
import deckbuilder.mtg.http.rest.DeckIdResource;
import deckbuilder.mtg.http.rest.DeckResource;
import deckbuilder.mtg.http.rest.JpaNoResultExceptionMapper;
import deckbuilder.mtg.http.rest.NoResultExceptionMapper;
import deckbuilder.mtg.http.rest.UserIdResource;
import deckbuilder.mtg.http.rest.UserResource;

public class HttpServer {
	private Injector appInjector;
	private int port = 8080;
	
	@Inject
	public HttpServer(Injector appInjector) {
		this.appInjector = appInjector;
	}
	
	@Inject 
	public void setPort(@Named("HTTP Port") int port) {
		this.port = port;
	}
	
	public int getPort() {
		return this.port;
	}
	
	public void startServer() throws Exception {
		Server server = new Server(getPort());
		
		ServletContextHandler servletContextHandler = new ServletContextHandler();
		
		//needed by jetty
		servletContextHandler.addServlet(DefaultServlet.class, "/");
		
		//add guice
		servletContextHandler.addEventListener(new DeckBuilderGuiceServletConfig(appInjector));
		servletContextHandler.addFilter(GuiceFilter.class, "/*", null);
		
		server.setHandler(servletContextHandler);
		server.start();
		server.join();
	}
	
	
	private static class DeckBuilderGuiceServletConfig extends GuiceServletContextListener {
		private final Injector appInjector;
		
		public DeckBuilderGuiceServletConfig(@Nonnull Injector appInjector) {
			assert appInjector != null;
			this.appInjector = appInjector;
		}
		
		@Override
		protected Injector getInjector() {
			
			//creates a new application injector for servlets with the application
			//injector as the parent
			return appInjector.createChildInjector(new ServletModule(){
				@Override
				protected void configureServlets() {
					//facebook
					bind(FacebookService.class).in(Singleton.class);
					bind(SecurityFilter.class).in(Singleton.class);
					bind(FacebookAuthenticationProvider.class).in(Singleton.class);
					
					//setup the basic servlet for getting access tokens from facebook 
					serve("/facebook/auth").with(FacebookAuthServlet.class);

					//setup persistence for all resources
					filter("/*").through(PersistFilter.class);
					
					//setup the cross-origin filter
					final Map<String, String> crossOriginFilterInitParams = createCrossOriginFilterInitParams();
					bind(CrossOriginFilter.class).in(Singleton.class);
					filter("/*").through(CrossOriginFilter.class, crossOriginFilterInitParams);
					
					//setup to serve all resources from the guice container
					Map<String, String> params = new HashMap<String, String>();
					params.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
					params.put(ResourceConfig.FEATURE_DISABLE_WADL, "true");
					params.put(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS, SecurityFilter.class.getName());
					bind(GuiceContainer.class);
					serve("/*").with(GuiceContainer.class, params);
				}
				
				/**
				 * Creates the initialization parameters for the CrossOriginFilter
				 */
				private Map<String, String> createCrossOriginFilterInitParams() {
					final Configuration config = appInjector.getInstance(Configuration.class);
					final Map<String, String> crossOriginFilterInitParams = Maps.newHashMap();
					crossOriginFilterInitParams.put("allowedOrigins", config.getCors().allowedOrigins);
					crossOriginFilterInitParams.put("allowedMethods", "GET,POST,HEAD,PATCH,DELETE,OPTIONS");
					crossOriginFilterInitParams.put("allowedHeaders", "Authorization,authorization");
					return crossOriginFilterInitParams;
				}
			},new JerseyServletModule(){
				@Override
				protected void configureServlets() {
					//exception mappers
					bind(AuthenticationExceptionMapper.class).in(Singleton.class);
					bind(JpaNoResultExceptionMapper.class).in(Singleton.class);
					bind(NoResultExceptionMapper.class).in(Singleton.class);
					
					//resources
					bind(CardResource.class).in(Singleton.class);
					bind(CardIdResource.class).in(Singleton.class);
					bind(CardSetResource.class).in(Singleton.class);
					bind(CardSetIdResource.class).in(Singleton.class);
					bind(CardSetIdCardsResource.class).in(Singleton.class);
					bind(DeckResource.class).in(Singleton.class);
					bind(DeckIdResource.class).in(Singleton.class);
					bind(DeckIdCardsResource.class).in(Singleton.class);
					bind(DeckCardResource.class).in(Singleton.class);
					bind(UserResource.class).in(Singleton.class);
					bind(UserIdResource.class).in(Singleton.class);
				}
				
				@Provides
				@Singleton 
				@Named("FacebookAppId")
				public String getFacebookAppId() {
					final String apiKey = System.getenv("FACEBOOK_APP_ID");
					
					if(Strings.isNullOrEmpty(apiKey)) {
						throw new RuntimeException("FACEBOOK_APP_ID must be provided as a environment variable");
					}
					
					return apiKey;
				}
				
				@Provides
				@Singleton
				@Named("FacebookSecret")
				public String getFacebookSecret() {
					final String secret = System.getenv("FACEBOOK_SECRET");

					if(Strings.isNullOrEmpty(secret)) {
						throw new RuntimeException("FACEBOOK_SECRET must be provided as a environment variable");
					}
					
					return secret;
				}
			});
		}
	}
}
