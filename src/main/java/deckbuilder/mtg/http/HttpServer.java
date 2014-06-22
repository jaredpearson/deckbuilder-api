package deckbuilder.mtg.http;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.google.common.base.Strings;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.persist.PersistFilter;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

import deckbuilder.mtg.facebook.FacebookService;
import deckbuilder.mtg.http.rest.CardResource;
import deckbuilder.mtg.http.rest.CardSetResource;
import deckbuilder.mtg.http.rest.DeckCardResource;
import deckbuilder.mtg.http.rest.DeckResource;
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
		private Injector appInjector;
		
		public DeckBuilderGuiceServletConfig(Injector appInjector) {
			this.appInjector = appInjector;
		}
		
		@Override
		protected Injector getInjector() {
			
			//creates a new application injector for servlets with the application
			//injector as the parent
			return appInjector.createChildInjector(new JerseyServletModule(){
				@Override
				protected void configureServlets() {
					//exception mappers
					bind(AuthenticationExceptionMapper.class).in(Singleton.class);
					
					//facebook
					bind(FacebookService.class).in(Singleton.class);
					
					//security
					bind(AuthenticationExceptionMapper.class).in(Singleton.class);
					bind(SecurityFilter.class).in(Singleton.class);
					bind(FacebookAuthenticationProvider.class).in(Singleton.class);
					
					//setup the basic servlet for getting access tokens from facebook 
					serve("/facebook/auth").with(FacebookAuthServlet.class);
					
					//controllers
					bind(CardResource.class).in(Singleton.class);
					bind(DeckResource.class).in(Singleton.class);
					bind(DeckCardResource.class).in(Singleton.class);
					bind(CardSetResource.class).in(Singleton.class);
					bind(UserResource.class).in(Singleton.class);
					
					//setup persistence for all resources
					filter("/*").through(PersistFilter.class);
					
					//setup to serve all resources from the guice container
					Map<String, String> params = new HashMap<String, String>();
					params.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
					params.put(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS, SecurityFilter.class.getName());
					bind(GuiceContainer.class);
					serve("/*").with(GuiceContainer.class, params);
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
