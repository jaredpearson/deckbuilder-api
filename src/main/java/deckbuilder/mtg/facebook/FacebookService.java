package deckbuilder.mtg.facebook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

@Singleton
public class FacebookService {
	private final String apiKey;
	private final String secret;
	
	/**
	 * Creates a new FacebookService with the specified API key and secret. These are provided
	 * by Facebook when creating a new application and used when authenticating clients.
	 */
	@Inject
	public FacebookService(@Named("FacebookAppId") String apiKey, @Named("FacebookSecret") String secret) {
		this.apiKey = apiKey;
		this.secret = secret;
	}
	
	/**
	 * Gets the URL for to request a code from Facebook.
	 */
	public String getRequestCodeUrl(String returnUrl) {
		try {
			return "https://www.facebook.com/dialog/oauth?" + 
			    "client_id=" + apiKey + 
			    "&redirect_uri=" + URLEncoder.encode(returnUrl, "UTF-8");
		} catch (UnsupportedEncodingException exc) {
			throw new RuntimeException(exc);
		}
	}
	
	/**
	 * Given an access token, the user's information is retrieved from Facebook
	 */
	public Map<String, Object> getUserInfo(String accessToken) {
		try {
			JsonNode json = getResponseAsJson("https://graph.facebook.com/me?access_token=" + accessToken);
			
			HashMap<String, Object> properties = new HashMap<>();
			properties.put("id", Long.parseLong(json.path("id").asText()));
			properties.put("username", json.path("username").asText());
			
			return properties;
		} catch (IOException exc) {
			throw new RuntimeException(exc);
		}
	}
	
	/**
	 * Given an access code, an access token is requested.
	 */
	public String getAccessToken(String code, String returnUrl) {
		try {
			String url = "https://graph.facebook.com/oauth/access_token?" + 
					"client_id=" + apiKey + 
					"&redirect_uri=" + URLEncoder.encode(returnUrl, "UTF-8") + 
					"&client_secret=" + secret + 
					"&code=" + code;
			return (String) request(url, new ConnectionHandler() {
				@Override
				public Object handle(HttpURLConnection connection) throws IOException {
					try(InputStream inputStream = connection.getInputStream()) {
						String responseBody = inputStreamToString(inputStream);
						Map<String, String> responseParams = parseQueryString(responseBody);
						return responseParams.get("access_token");
					}
				}
			});
		} catch(IOException exc) {
			throw new RuntimeException(exc);
		}
	}
	
	private String inputStreamToString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int r;
        while ((r = inputStream.read()) != -1) {
            baos.write(r);
        }
        return new String(baos.toByteArray());
	}
	
	private JsonNode getResponseAsJson(String url) throws IOException {
		return (JsonNode) request(url, new ConnectionHandler() {
			@Override
			public Object handle(HttpURLConnection connection) throws IOException {
				try(InputStream inputStream = connection.getInputStream()) {
					ObjectMapper mapper = new ObjectMapper();
					JsonNode node = mapper.readTree(inputStream);
	    			return node;
				}
			}
		});
	}
	
	private Object request(String url, ConnectionHandler handler) throws IOException {
		HttpURLConnection connection = null;
		try {
			URL u = new URL(url);
			connection = (HttpURLConnection) u.openConnection();
			connection.connect();
			
			if(connection.getResponseCode() == 200) {
				return handler.handle(connection);
			} else {
				try(InputStream inputStream = connection.getErrorStream()) {
					String responseBody = inputStreamToString(inputStream);
					throw new RuntimeException(responseBody);
				}
			}
			
		} finally {
			if(connection != null) {
				connection.disconnect();
			}
		}
	}
	
	private Map<String, String> parseQueryString(String value) {
		HashMap<String, String> params = new HashMap<>(); 
		
        String[] pairs = value.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if(kv.length == 2) {
            	String key = kv[0];
            	String paramValue = kv[1];
            	params.put(key, paramValue);
            }
        }
        
        return params;
	}
	
	private interface ConnectionHandler {
		public Object handle(HttpURLConnection connection) throws IOException;
	}
}