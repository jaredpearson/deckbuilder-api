package deckbuilder.mtg.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import deckbuilder.mtg.http.HttpServer;

/**
 * Configuration for the {@link HttpServer} bean (and related settings)
 * @author jared.pearson
 */
@Configuration
public class HttpServerConfig {
	
	@Autowired
	ApplicationContext appContext;
	
	@Bean
	public HttpServer httpServer() {
		return new HttpServer(appContext);
	}
	
	@Bean(name = "HTTP Port")
	public int getHttpPort() {
		
		//default to port 8080
		int port = 8080;
		
		//attempt to get the port from the environment variable
		String portString = System.getenv("PORT");
		if(portString != null) {
			port = Integer.valueOf(portString);
		}
		
		return port;
	}
}