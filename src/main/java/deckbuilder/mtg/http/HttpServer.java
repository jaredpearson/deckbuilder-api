package deckbuilder.mtg.http;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.HttpRequestHandlerServlet;
import org.springframework.web.servlet.DispatcherServlet;

import com.google.common.collect.Maps;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;

import deckbuilder.mtg.Configuration;

/**
 * Manages the starting of the HTTP server
 * @author jared.pearson
 */
public class HttpServer {
	private ApplicationContext applicationContext;
	private int port = 8080;
	
	@Inject
	public HttpServer(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	@Inject
	public void setPort(@Named("HTTP Port") int port) {
		this.port = port;
	}
	
	public int getPort() {
		return this.port;
	}
	
	/**
	 * Starts the HTTP server
	 */
	public void startServer() throws Exception {
		final ServletContextHandler servletContextHandler = new ServletContextHandler();
		
		// add the facebook auth servlet
		servletContextHandler.addServlet(createFacebookServletHolder(), "/facebook/auth");
		
		// needed by jetty
		servletContextHandler.addServlet(DefaultServlet.class, "/");
		
		// add spring
		WebApplicationContext webAppContext = createWebApplicationContext();
		servletContextHandler.addEventListener(new ContextLoaderListener(webAppContext));
		servletContextHandler.addServlet(new ServletHolder(new DispatcherServlet(webAppContext)), "/*");
		
		// add jersey
		servletContextHandler.addServlet(createJerseyServletHolder(), "/*");
		
		// add the cross origin filter
		final Configuration config = applicationContext.getBean(Configuration.class);
		servletContextHandler.addFilter(createCrossOriginFilterHolder(config), "/*", EnumSet.allOf(DispatcherType.class));

		// create the Jetty server
		final Server server = new Server(getPort());
		server.setHandler(servletContextHandler);
		server.start();
		server.join();
	}
	
	private WebApplicationContext createWebApplicationContext() {
		final AnnotationConfigWebApplicationContext webAppContext = new AnnotationConfigWebApplicationContext();
		webAppContext.setParent(applicationContext);
		webAppContext.scan("deckbuilder.mtg.http.config");
		return webAppContext;
	}
	
	private ServletHolder createFacebookServletHolder() {
		final ServletHolder servletHolder = new ServletHolder(HttpRequestHandlerServlet.class);
		servletHolder.setName("facebookAuthServlet");
		servletHolder.setDisplayName("FacebookAuthServlet");
		return servletHolder;
	}
	
	private ServletHolder createJerseyServletHolder() {
		final Map<String, String> params = new HashMap<String, String>();
		params.put(JSONConfiguration.FEATURE_POJO_MAPPING, "true");
		params.put(ResourceConfig.FEATURE_DISABLE_WADL, "true");
		params.put(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS, SecurityFilter.class.getName());
		
		final ServletHolder jerseyServletHolder = new ServletHolder(SpringServlet.class);
		jerseyServletHolder.setInitParameters(params);
		return jerseyServletHolder;
	}
	
	private FilterHolder createCrossOriginFilterHolder(Configuration config) {
		final Map<String, String> crossOriginFilterInitParams = Maps.newHashMap();
		crossOriginFilterInitParams.put(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, config.cors.allowedOrigins);
		crossOriginFilterInitParams.put(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD,PATCH,DELETE,OPTIONS");
		crossOriginFilterInitParams.put(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "accept,authorization,content-type");
		crossOriginFilterInitParams.put(CrossOriginFilter.CHAIN_PREFLIGHT_PARAM, "false");
		
		final FilterHolder crossOriginFilterHolder = new FilterHolder(CrossOriginFilter.class);
		crossOriginFilterHolder.setInitParameters(crossOriginFilterInitParams);
		return crossOriginFilterHolder;
	}
}
