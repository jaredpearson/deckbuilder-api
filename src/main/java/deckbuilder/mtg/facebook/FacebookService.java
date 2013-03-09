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

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.base.Strings;
import com.google.inject.Singleton;

@Singleton
public class FacebookService {
	private static String API_KEY;
	private static String SECRET;
	
	static {
		//load the appId and secret from System variables
		API_KEY = System.getenv("FACEBOOK_APP_ID");
		SECRET = System.getenv("FACEBOOK_SECRET");
		
		if(Strings.isNullOrEmpty(API_KEY)) {
			throw new RuntimeException("FACEBOOK_APP_ID must be provided as a environment variable");
		}
		if(Strings.isNullOrEmpty(SECRET)) {
			throw new RuntimeException("FACEBOOK_SECRET must be provided as a environment variable");
		}
	}
	
	/**
	 * Gets the URL for to request a code from Facebook.
	 */
	public String getRequestCodeUrl(String returnUrl) {
		try {
			return "https://www.facebook.com/dialog/oauth?" + 
			    "client_id=" + API_KEY + 
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
			properties.put("id", json.path("id").asText());
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
					"client_id=" + API_KEY + 
					"&redirect_uri=" + URLEncoder.encode(returnUrl, "UTF-8") + 
					"&client_secret=" + SECRET + 
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