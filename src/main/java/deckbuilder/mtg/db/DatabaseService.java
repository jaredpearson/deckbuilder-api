package deckbuilder.mtg.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;

import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import com.google.inject.Inject;

public class DatabaseService {
	private static final Logger logger = Logger.getLogger(DatabaseService.class.getName());
	private static final String PATH_PREFIX_JAR = "jar:";
	private static final String FILE_PREFIX_JAR = "file:";
	private DataSource dataSource;
	
	@Inject
	public DatabaseService(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * Initializes the database with the schema.
	 */
	public void initializeSchema() throws SQLException, IOException {
		//list of ddl scripts to be executed in order
		String ddlDir = "/deckbuilder/mtg/db/";
		String[] ddlScripts = new String[]{
			"users.sql",
			"sets.sql",
			"cards.sql",
			"decks.sql",
			"deckcards.sql"
		};
		ScriptParser scriptParser = new ScriptParser();
		try(Connection cnn = dataSource.getConnection()) {
			try(Statement stmt = cnn.createStatement()) {
				for(String ddlScript : ddlScripts) {
					logger.fine("Executing " + ddlScript);
					String fullScriptPath = ddlDir + ddlScript;
					try(InputStream inputStream = DatabaseService.class.getResourceAsStream(fullScriptPath)) {
						if(inputStream == null) {
							throw new RuntimeException("Unable to find DDL script at path: " + fullScriptPath);
						}
						
						try(InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"))) {
							String script = CharStreams.toString(inputStreamReader);
							for(String statement : scriptParser.parseScript(script)) {
								stmt.execute(statement);
							}
						}
					}
				}
			}
		}
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
						stmt.setString(1, Strings.emptyToNull(setNode.path("name").asText()));
						stmt.setString(2, Strings.emptyToNull(setNode.path("abbreviation").asText()));
						stmt.setString(3, Strings.emptyToNull(setNode.path("language").asText()));
						
						stmt.executeUpdate();
						ResultSet rst = stmt.getGeneratedKeys();
						if(rst.next()) {
							setId = rst.getLong(1);
						}
					}
					
					ArrayNode cardsNode = (ArrayNode)setNode.path("cards");
					
					try(PreparedStatement stmt = cnn.prepareStatement("insert into Cards(name, powerToughness, castingCost, typeLine, text, picUrl, cardSet, setIndex) values (?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
						for(JsonNode cardNode : cardsNode) {
							try {
								stmt.clearParameters();
								stmt.setString(1, Strings.emptyToNull(cardNode.path("name").asText()));
								stmt.setString(2, Strings.emptyToNull(cardNode.path("powerToughness").asText()));
								stmt.setString(3, Strings.emptyToNull(cardNode.path("castingCost").asText()));
								stmt.setString(4, Strings.emptyToNull(cardNode.path("typeLine").asText()));
								stmt.setNull(5, Types.VARCHAR); //text
								stmt.setNull(6, Types.VARCHAR); //picUrl
								stmt.setLong(7, setId);
								stmt.setString(8, Strings.emptyToNull(cardNode.path("index").asText()));
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