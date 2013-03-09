package deckbuilder.mtg.http;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.persist.PersistFilter;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

import deckbuilder.mtg.facebook.FacebookService;
import deckbuilder.mtg.http.rest.CardController;
import deckbuilder.mtg.http.rest.CardSetController;
import deckbuilder.mtg.http.rest.DeckCardController;
import deckbuilder.mtg.http.rest.DeckController;

public class HttpServer {
	public void startServer(Injector appInjector) throws Exception {
		Server server = new Server(8080);
		
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
			return appInjector.createChildInjector(new JerseyServletModule(){
				@Override
				protected void configureServlets() {
					//exception mappers
					bind(AuthenticationExceptionMapper.class).in(Singleton.class);
					
					//security
					bind(AuthenticationExceptionMapper.class).in(Singleton.class);
					bind(SecurityFilter.class).in(Singleton.class);
					bind(FacebookService.class).in(Singleton.class);
					bind(FacebookAuthenticationProvider.class).in(Singleton.class);
					
					//setup the basic servlet for getting access tokens from facebook 
					serve("/facebook/auth").with(FacebookAuthServlet.class);
					
					//controllers
					bind(CardController.class).in(Singleton.class);
					bind(DeckController.class).in(Singleton.class);
					bind(DeckCardController.class).in(Singleton.class);
					bind(CardSetController.class).in(Singleton.class);
					
					//setup persistence for all resources
					filter("/*").through(PersistFilter.class);
					
					//setup to serve all resources from the guice container
					Map<String, String> params = new HashMap<String, String>();
					params.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
					params.put(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS, SecurityFilter.class.getName());
					bind(GuiceContainer.class);
					serve("/*").with(GuiceContainer.class, params);
				}
			});
		}
	}
}
