package deckbuilder.mtg;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import junit.framework.Assert;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * Integration test for the main functionality
 * @author jared.pearson
 */
public class DeckbuilderIT {
	private static HttpTestUtils httpUtils;
	
	@BeforeClass
	public static void setupHttpUtils() throws IOException {
		// load up the integration config properties
		Properties integrationTestProperties = new Properties();
		try(final InputStream propertiesStream = DeckbuilderIT.class.getResourceAsStream("integration-test.properties")) {
			integrationTestProperties.load(propertiesStream);
		}
		
		// verify that the values are correct
		if (Strings.nullToEmpty(integrationTestProperties.getProperty("serverUrl")).trim().isEmpty()) {
			throw new IllegalStateException("serverUrl is not specified");
		}
		
		final String authToken = "Facebook-Access-Token " + integrationTestProperties.getProperty("facebookAccessToken");
		
		// create the utils
		httpUtils = new HttpTestUtils(integrationTestProperties.getProperty("serverUrl"), authToken);
	}
	
	@Test
	public void testEmptyDeck() throws Exception {
		Response response = httpUtils.get("/v1/deck");
		Assert.assertEquals(200, response.getStatusCode());
		Assert.assertEquals("[]", response.getResponseBody());
	}
	
	@Test
	public void testSetResource() throws Exception {
		Number newSetId = null; 
		final String cardSetName = "Test" + System.currentTimeMillis();
		final String cardSetAbbr = "tst";
		final String cardSetLanguage = "en";
		final Map<String, Object> newSet = new HashMap<String, Object>();
		newSet.put("name", cardSetName);
		newSet.put("abbreviation", cardSetAbbr);
		newSet.put("language", cardSetLanguage);
		
		try {
			// create a new set. the user must be an administrator to do this
			final Response createSetResponse = httpUtils.postJson("/v1/set", newSet);
			Assert.assertEquals(200, createSetResponse.getStatusCode());
			final JsonObjectNode createSetResponseNode = (JsonObjectNode)createSetResponse.getResponseBodyAsJsonObject();
			Assert.assertTrue(createSetResponseNode.hasProperty("id"));
			newSetId = createSetResponseNode.getValueAsNumber("id");
			Assert.assertNotNull(newSetId);
			
			// read the sets back out and make sure the set can be found
			final Response response = httpUtils.get("/v1/set");
			Assert.assertEquals(200, response.getStatusCode());
			
			final JsonObjectNode node = (JsonObjectNode)response.getResponseBodyAsJsonObject(); 
			Assert.assertTrue(node.hasProperty("cardSets"));
			
			final JsonArrayNode cardSetsNode = (JsonArrayNode)node.get("cardSets");
			Assert.assertNotNull(cardSetsNode);
			
			// make sure the set can be found
			boolean found = false;
			for (JsonNode childNode : cardSetsNode.asList()) {
				final JsonObjectNode childObjectNode = (JsonObjectNode)childNode;
				if (!childObjectNode.getValueAsNumber("id").equals(newSetId)) {
					continue;
				}
				found = true;
				Assert.assertTrue(childObjectNode.hasProperty("name"));
				Assert.assertEquals(childObjectNode.getValueAsString("name"), cardSetName);
				Assert.assertTrue(childObjectNode.hasProperty("abbreviation"));
				Assert.assertEquals(childObjectNode.getValueAsString("abbreviation"), cardSetAbbr);
				Assert.assertTrue(childObjectNode.hasProperty("language"));
				Assert.assertEquals(childObjectNode.getValueAsString("language"), cardSetLanguage);
			}
			Assert.assertTrue("Expected the new card set to be found", found);
		} finally {
			
		}
	}
	
	/**
	 * Utilities for making HTTP requests
	 * @author jared.pearson
	 */
	public static class HttpTestUtils {
		private final String serverUrl;
		private final String authToken;
		
		public HttpTestUtils(String serverUrl, String authToken) {
			assert serverUrl != null;
			assert authToken != null;
			this.serverUrl = serverUrl;
			this.authToken = authToken;
		}
	
		/**
		 * Posts the specified map to the specified URL in a JSON format
		 * @param path the path to execute the POST
		 * @param content the content to be posted as JSON
		 * @return the response from the server
		 * @throws IOException
		 */
		public Response postJson(final String path, Map<String, Object> content) throws IOException {
			final HttpPost request = new HttpPost(serverUrl + path);
			request.setHeader("Authorization", authToken);
			request.setHeader("Content-type", "application/json;charset=UTF-8");
			request.setEntity(new StringEntity(new ObjectMapper().writeValueAsString(content)));
			return executeRequest(request);
		}
		
		/**
		 * Executes a GET request against the specified path
		 * @param path the path to execute the GET
		 * @return the response from the server
		 * @throws IOException
		 */
		public Response get(final String path) throws IOException {
			final HttpGet request = new HttpGet(serverUrl + path);
			request.setHeader("Authorization", authToken);
			return executeRequest(request);
		}
		
		private Response executeRequest(HttpUriRequest request) throws IOException {
			try(final CloseableHttpClient client = HttpClients.createDefault()) {
				try(final CloseableHttpResponse response = client.execute(request)) {
					final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					response.getEntity().writeTo(outputStream);
					return new Response(response.getStatusLine().getStatusCode(), outputStream.toString());
				}
			}
		}
	}
	
	/**
	 * Represents the response returned by the server
	 * @author jared.pearson
	 */
	private static class Response {
		private final int statusCode;
		private final String responseBody;
		private JsonObjectNode jsonObject;
		
		public Response(int statusCode, String responseBody) {
			this.statusCode = statusCode;
			this.responseBody = responseBody;
		}
		
		/**
		 * Gets the status code of the response
		 */
		public int getStatusCode() {
			return this.statusCode;
		}
		
		/**
		 * Gets the body of the response
		 */
		public String getResponseBody() {
			return this.responseBody;
		}
		
		/**
		 * Converts the response body into a JSON object.
		 */
		@SuppressWarnings("unchecked")
		public JsonNode getResponseBodyAsJsonObject() {
			if (jsonObject == null) {
				try {
					this.jsonObject = new JsonObjectNode(new ObjectMapper().readValue(this.responseBody, Map.class));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			} 
			return jsonObject;
		}
	}
	
	private static abstract class JsonNode {
		/**
		 * Gets a new JsonNode of the type for the specified value.
		 */
		@SuppressWarnings("unchecked")
		protected JsonNode toNode(final Object value) {
			if (value instanceof Number) {
				return new JsonNumberNode((Number)value);
			} else if (value instanceof Map) {
				return new JsonObjectNode((Map<String, Object>)value);
			} else if (value instanceof List) {
				final List<Object> valueList = (List<Object>)value;
				final List<JsonNode> nodeList = Lists.newArrayListWithCapacity(valueList.size());
				for (Object item : valueList) {
					nodeList.add(toNode(item));
				}
				return new JsonArrayNode(nodeList);
			}
			throw new IllegalStateException("Unknown type: " + value.getClass());
		}
	}
	
	/**
	 * Json node representing a number
	 * @author jared.pearson
	 */
	private static class JsonNumberNode extends JsonNode {
		private final Number value;
		
		public JsonNumberNode(Number value) {
			this.value = value;
		}
		
		public Number asNumber() {
			return value;
		}
	}
	
	/**
	 * Json node representing an Array
	 * @author jared.pearson
	 */
	private static class JsonArrayNode extends JsonNode {
		private final List<JsonNode> values;
		
		public JsonArrayNode(List<JsonNode> values) {
			this.values = values;
		}
		
		public List<JsonNode> asList() {
			return this.values;
		}
	}
	
	/**
	 * Json node representing an Object
	 * @author jared.pearson
	 */
	private static class JsonObjectNode extends JsonNode {
		private final Map<String, Object> properties;
		
		public JsonObjectNode(Map<String, Object> properties) {
			this.properties = properties;
		}
		
		/**
		 * Gets the value of the property with the specified name. if the property
		 * does not exist, then an {@link IllegalArgumentException} is thrown.
		 * @param name the name of the property
		 */
		public JsonNode get(String name) {
			if (!hasProperty(name)) {
				throw new IllegalArgumentException("Unknown property name specified: " + name);
			}
			final Object value = this.properties.get(name);
			return toNode(value);
		}
		
		/**
		 * Determines if the specified property is found in the object
		 */
		public boolean hasProperty(String name) {
			return this.properties.containsKey(name);
		}
		
		/**
		 * Convenience method to get a property value as a number. 
		 * <p>
		 * This is the same as
		 * <pre>((JsonNumberNode)get(name)).asNumber()</pre>
		 * @param name the name of the property
		 */
		public Number getValueAsNumber(String name) {
			return ((JsonNumberNode)get(name)).asNumber();
		}

		/**
		 * Convenience method to get a property value as a list of nodes. 
		 * <p>
		 * This is the same as
		 * <pre>((JsonNumberNode)get(name)).asList()</pre>
		 * @param name the name of the property
		 */
		public List<JsonNode> getValueAsArray(String name) {
			return ((JsonArrayNode)get(name)).asList();
		}
		
		/**
		 * Gets the value of a property as a string.
		 */
		public String getValueAsString(String name) {
			return (String)this.properties.get(name);
		}
		
		@Override
		public String toString() {
			try {
				return new ObjectMapper().writeValueAsString(properties);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
