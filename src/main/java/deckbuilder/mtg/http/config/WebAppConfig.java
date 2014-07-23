package deckbuilder.mtg.http.config;

import javax.servlet.http.HttpServlet;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.google.common.base.Strings;

import deckbuilder.mtg.facebook.FacebookService;
import deckbuilder.mtg.http.FacebookAuthServlet;
import deckbuilder.mtg.http.FacebookAuthenticationProvider;

/**
 * Configuration for the Servlets/Filters and beans used within the general 
 * web application.
 * <p>
 * Note: The use SCOPE_SINGLETON is because the Spring-to-Jersey integration
 * uses the annotation value. The default scope (value of "") is unknown to 
 * the Spring-to-Jersey integration and fails.
 * @author jared.pearson
 */
@EnableWebMvc
@Configuration
public class WebAppConfig extends WebMvcConfigurerAdapter {
	
	@Bean
	@Scope(BeanDefinition.SCOPE_SINGLETON)
	public FacebookService facebookService() {
		return new FacebookService(facebookAppId(), facebookSecret());
	}
	
	@Bean
	@Scope(BeanDefinition.SCOPE_SINGLETON)
	public FacebookAuthenticationProvider facebookAuthenticationProvider() {
		return new FacebookAuthenticationProvider();
	}
	
	@Bean(name = "FacebookAppId")
	@Scope(BeanDefinition.SCOPE_SINGLETON)
	public String facebookAppId() {
		final String apiKey = System.getenv("FACEBOOK_APP_ID");
		
		if(Strings.isNullOrEmpty(apiKey)) {
			throw new RuntimeException("FACEBOOK_APP_ID must be provided as a environment variable");
		}
		
		return apiKey;
	}
	
	@Bean(name = "FacebookSecret")
	@Scope(BeanDefinition.SCOPE_SINGLETON)
	public String facebookSecret() {
		final String secret = System.getenv("FACEBOOK_SECRET");

		if(Strings.isNullOrEmpty(secret)) {
			throw new RuntimeException("FACEBOOK_SECRET must be provided as a environment variable");
		}
		
		return secret;
	}
	
	@Bean
	@Scope(BeanDefinition.SCOPE_SINGLETON)
	public HttpServlet facebookAuthServlet() {
		return new FacebookAuthServlet();
	}
}