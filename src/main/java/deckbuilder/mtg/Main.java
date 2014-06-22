package deckbuilder.mtg;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.hsqldb.jdbc.JDBCDataSourceFactory;

import com.google.common.base.Strings;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.persist.jpa.JpaPersistModule;

import deckbuilder.mtg.Configuration.CorsConfiguration;
import deckbuilder.mtg.db.DatabaseCredentials;
import deckbuilder.mtg.db.DatabaseService;
import deckbuilder.mtg.db.DatabaseUrlParser;
import deckbuilder.mtg.http.HttpServer;
import deckbuilder.mtg.service.CardService;
import deckbuilder.mtg.service.CardSetService;
import deckbuilder.mtg.service.DeckCardService;
import deckbuilder.mtg.service.DeckService;
import deckbuilder.mtg.service.UserService;
import deckbuilder.mtg.service.impl.DefaultCardService;
import deckbuilder.mtg.service.impl.DefaultCardSetService;
import deckbuilder.mtg.service.impl.DefaultDeckCardService;
import deckbuilder.mtg.service.impl.DefaultDeckService;
import deckbuilder.mtg.service.impl.DefaultUserService;

public class Main {
	private static final Command COMMAND_RUN = new Command("run", "Starts the server");
	private static final Command COMMAND_DBINIT = new Command("dbinit", "Initializes the database by executing the DDL");
	private static final Command COMMAND_DBIMPORTSETS = new Command("dbimportsets", "Imports the set data from the files specified as arguments.");
	private static final Command[] COMMANDS;
	
	static {
		COMMANDS = new Command[]{
			COMMAND_RUN,
			COMMAND_DBINIT,
			COMMAND_DBIMPORTSETS
		};
	}
	
	private Main() {
	}

	public static void main(String[] args) throws Exception {
		if(args == null || args.length == 0) {
			printUsage(System.out);
			return;
		}
		
		Command command = getCommand(args[0]);
		if(command == null) {
			System.out.println("Unknown command specified: " + args[0]);
			printUsage(System.out);
			return;
		}
		
		String[] commandArgs = (args.length == 1) ? new String[0] : Arrays.copyOfRange(args, 1, args.length);
		
		if(COMMAND_RUN.equals(command)) {
			HttpServer httpServer = createAppInjector().getInstance(HttpServer.class);
			httpServer.startServer();
			
		} else if(COMMAND_DBINIT.equals(command)) {
			DatabaseService dbService = createAppInjector().getInstance(DatabaseService.class);
			dbService.initializeSchema();
			
		} else if(COMMAND_DBIMPORTSETS.equals(command)) {
			if(commandArgs.length == 0) {
				System.out.println("No files passed as arguments.");
				printUsage(System.out);
				return;
			}
			String[] setPaths = commandArgs;
			DatabaseService dbService = createAppInjector().getInstance(DatabaseService.class);
			dbService.importSets(setPaths);
			
		} else {
			throw new IllegalStateException();
		}
	}

	/**
	 * Prints the usage information to the given print stream.
	 */
	private static void printUsage(PrintStream out) {
		int maxNameLength = -1;
		for(Command command : COMMANDS) {
			if(command.getCommand().length() > maxNameLength) {
				maxNameLength = command.getCommand().length();
			}
		}
		
		out.println("usage: java " + HttpServer.class.getName() + " <command> [args*]");
		out.println();
		out.println("commands:");
		for(Command command : COMMANDS) {
			out.print("  ");
			out.print(Strings.padEnd(command.getCommand(), maxNameLength, ' '));
			out.print("    ");
			out.print(command.getHelpText());
			out.println();
		}
	}
	
	private static Command getCommand(String text) {
		if(text == null) {
			return null;
		}
		
		for(Command command : COMMANDS) {
			if(command.getCommand().equals(text.trim())) {
				return command;
			}
		}
		return null;
	}
	
	private static Injector createAppInjector() {
		//the database url is given in following format
		// [type]://[username]:[password]@[host]:[port]
		String databaseUrl = System.getenv("DATABASE_URL");
		if(databaseUrl == null) {
			throw new RuntimeException("DATABASE_URL environment variable not found.");
		}
		final DatabaseCredentials dbCreds = (new DatabaseUrlParser()).parseDatabaseUrl(databaseUrl);
		
		//setup the persitance module
		JpaPersistModule persistModule = new JpaPersistModule("deckbuilder.mtg");
		Properties properties = new Properties();
		properties.put("hibernate.dialect", dbCreds.getHibernateDialect());
	    properties.put("javax.persistence.jdbc.driver", dbCreds.getJdbcDriverClassName());
	    properties.put("javax.persistence.jdbc.url", dbCreds.getJdbcUrl());
	    properties.put("javax.persistence.jdbc.user", dbCreds.getUsername());
	    properties.put("javax.persistence.jdbc.password", dbCreds.getPassword());
		persistModule.properties(properties);
		
		return Guice.createInjector(persistModule, 
				
			//Database/Datasource module
			new AbstractModule() {
				@Override
				protected void configure() {
					bind(DatabaseCredentials.class).toInstance(dbCreds);
					bind(DataSource.class).toProvider(HsqldbDataSourceProvider.class).in(Singleton.class);
					bind(DatabaseService.class).in(Singleton.class);
				}
			}, 
			
			//Application specific services module
			new AbstractModule() {
				@Override
				protected void configure() {
					bind(UserService.class).to(DefaultUserService.class).in(Singleton.class);
					bind(CardService.class).to(DefaultCardService.class).in(Singleton.class);
					bind(DeckService.class).to(DefaultDeckService.class).in(Singleton.class);
					bind(DeckCardService.class).to(DefaultDeckCardService.class).in(Singleton.class);
					bind(CardSetService.class).to(DefaultCardSetService.class).in(Singleton.class);
				}
				
				@Provides
				@Singleton
				private Configuration loadConfig() {
					//load the properties from the standard location
					Properties properties = new Properties();
					try(InputStream inputStream = getClass().getResourceAsStream("/deckbuilder/mtg/config.properties")) {
						properties.load(inputStream);
					} catch(IOException exc) {
						throw new RuntimeException(exc);
					}
					

					//get the administrators
					final List<String> administratorNames;
					final String adminNamesAsString = properties.getProperty("administrators");
					if(adminNamesAsString != null) {
						administratorNames = Arrays.asList(adminNamesAsString.split(","));
					} else {
						administratorNames = Collections.emptyList();
					}
					
					// get the cors.* properties
					final String corsAllowedOrigins = properties.getProperty("cors.allowedOrigins", ""); 
					CorsConfiguration corsConfig = new CorsConfiguration(corsAllowedOrigins);
					
					return new Configuration(administratorNames, corsConfig);
				}
			},
			
			//HTTP Server module
			new AbstractModule() {
				@Override
				protected void configure() {
					bind(HttpServer.class).in(Singleton.class);
				}
				
				@Provides 
				@Singleton
				@Named("HTTP Port")
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
			});
	}
	
	private static class HsqldbDataSourceProvider implements Provider<DataSource> {
		@Inject
		DatabaseCredentials credentials;
		
		@Override
		public DataSource get() {
			try {
				Properties dbProps = new Properties();
				dbProps.setProperty("url", credentials.getJdbcUrl());
				dbProps.setProperty("user", credentials.getUsername());
				dbProps.setProperty("password", credentials.getPassword());
				return JDBCDataSourceFactory.createDataSource(dbProps);
			} catch(Exception exc) {
				throw new RuntimeException(exc);
			}
		}
	}
	
	private static class Command {
		private String command;
		private String helpText;
		
		public Command(String command, String helpText) {
			this.command = command;
			this.helpText = helpText;
		}
		
		public String getCommand() {
			return command;
		}
		
		public String getHelpText() {
			return helpText;
		}
	}
}
