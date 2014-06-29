package deckbuilder.mtg.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import javax.sql.DataSource;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.flywaydb.core.Flyway;

import com.google.common.base.Strings;
import com.google.inject.Inject;

public class DatabaseService {
	private static final String PATH_PREFIX_JAR = "jar:";
	private static final String FILE_PREFIX_JAR = "file:";
	private final DataSource dataSource;
	
	@Inject
	public DatabaseService(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * Initializes the database with the schema.
	 */
	public void initializeSchema() throws SQLException, IOException {
		final Flyway flyway = new Flyway();
		flyway.setDataSource(dataSource);
		flyway.setLocations("classpath:deckbuilder.mtg.db");
		flyway.migrate();
	}
	
	/**
	 * Imports the files at the given paths as Set data.
	 * <p>
	 * The format of each file is a JSON file.
	 */
	public void importSets(String[] setPaths) throws SQLException, IOException {
		try(Connection cnn = dataSource.getConnection()) {
			for(String setPath : setPaths) {
				
				try(InputStream inputStream = openInputStreamFromPath(setPath)) {
					ObjectMapper mapper = new ObjectMapper();
					JsonNode setNode = mapper.readTree(inputStream);
					
					Long setId = null;
					
					try(PreparedStatement stmt = cnn.prepareStatement("insert into Sets(name, abbreviation, language) values (?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
						stmt.setString(1, asString(setNode, "name"));
						stmt.setString(2, asString(setNode, "abbreviation"));
						stmt.setString(3, asString(setNode, "language"));
						
						stmt.executeUpdate();
						ResultSet rst = stmt.getGeneratedKeys();
						if(rst.next()) {
							setId = rst.getLong(1);
						}
					}
					
					ArrayNode cardsNode = (ArrayNode)setNode.path("cards");
					
					try(PreparedStatement stmt = cnn.prepareStatement("insert into Cards(name, powerToughness, castingCost, typeLine, text, picUrl, cardSet, setIndex, rarity, artist) values (?,?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
						for(JsonNode cardNode : cardsNode) {
							try {
								stmt.clearParameters();
								stmt.setString(1, asString(cardNode, "name"));
								stmt.setString(2, asString(cardNode, "powerToughness"));
								stmt.setString(3, asString(cardNode, "castingCost"));
								stmt.setString(4, asString(cardNode, "typeLine"));
								stmt.setString(5, asString(cardNode, "body"));
								stmt.setNull(6, Types.VARCHAR); //picUrl
								stmt.setLong(7, setId);
								stmt.setString(8, asString(cardNode, "index"));
								stmt.setString(9, rarityToAbbr(asString(cardNode, "rarity")));
								stmt.setString(10, asString(cardNode, "artist"));
								stmt.executeUpdate();
							} catch(java.sql.SQLDataException exc) {
								System.err.println("Error while saving card: " + cardNode.toString());
								throw exc;
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Gets a string from the field on the JSON node.
	 */
	private static String asString(JsonNode node, String field) {
		return Strings.emptyToNull(node.path(field).asText());
	}
	
	/**
	 * Converts the full rarity name to the abbreviation
	 */
	private static String rarityToAbbr(String value) {
		if(value == null || value.trim().length() == 0) {
			return null;
		}
		return value.substring(0, 1).toUpperCase();
	}
	
	private static InputStream openInputStreamFromPath(String path) throws IOException {
		if(path.startsWith(PATH_PREFIX_JAR)) {
			return DatabaseService.class.getResourceAsStream(path.substring(PATH_PREFIX_JAR.length()));
		} else if(path.startsWith(FILE_PREFIX_JAR)) {
			return new FileInputStream(new File(path.substring(FILE_PREFIX_JAR.length())));
		} else {
			return new FileInputStream(new File(path));
		}
	}
}