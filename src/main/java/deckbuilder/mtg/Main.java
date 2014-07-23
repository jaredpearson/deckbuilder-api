package deckbuilder.mtg;

import java.io.PrintStream;
import java.util.Arrays;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.common.base.Strings;

import deckbuilder.mtg.db.DatabaseService;
import deckbuilder.mtg.http.HttpServer;

public class Main {
	private static final Command COMMAND_RUN = new Command("run", "Starts the server", new RunCommandRunnable());
	private static final Command COMMAND_DBINIT = new Command("dbinit", "Initializes the database by executing the DDL", new DbInitCommandRunnable());
	private static final Command COMMAND_DBIMPORTSETS = new Command("dbimportsets", "Imports the set data from the files specified as arguments.", new DbImportSetsCommandRunnable());
	private static final Command[] COMMANDS = new Command[]{
		COMMAND_RUN,
		COMMAND_DBINIT,
		COMMAND_DBIMPORTSETS
	};
	
	private Main() {
	}

	public static void main(String[] args) throws Exception {
		if(args == null || args.length == 0) {
			printUsage(System.out);
			return;
		}
		
		final Command command = getCommand(args[0]);
		if(command == null) {
			System.out.println("Unknown command specified: " + args[0]);
			printUsage(System.out);
			return;
		}
		
		final String[] commandArgs = (args.length == 1) ? new String[0] : Arrays.copyOfRange(args, 1, args.length);
		command.run(commandArgs);
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
	
	private static ApplicationContext createApplicationContext() {
		final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.scan("deckbuilder.mtg.config");
		context.refresh();
		return context;
	}
	
	private static class Command {
		private final String command;
		private final String helpText;
		private final CommandRunnable runnable;
		
		public Command(String command, String helpText, CommandRunnable runnable) {
			this.command = command;
			this.helpText = helpText;
			this.runnable = runnable;
		}
		
		public String getCommand() {
			return command;
		}
		
		public String getHelpText() {
			return helpText;
		}
		
		/**
		 * Executes the command
		 * @param args arguments for the command
		 */
		public void run(String[] args) throws Exception {
			runnable.run(args);
		}
		
		/**
		 * Closure for the execution of the command
		 * @author jared.pearson
		 */
		public interface CommandRunnable {
			public void run(String[] args) throws Exception;
		}
	}
	
	private static class RunCommandRunnable implements Command.CommandRunnable {
		@Override
		public void run(String[] args) throws Exception {
			final HttpServer httpServer = createApplicationContext().getBean(HttpServer.class);
			httpServer.startServer();
		}
	}
	
	private static class DbInitCommandRunnable implements Command.CommandRunnable {
		@Override
		public void run(String[] args) throws Exception {
			final DatabaseService dbService = createApplicationContext().getBean(DatabaseService.class);
			dbService.initializeSchema();
		}
	}
	
	private static class DbImportSetsCommandRunnable implements Command.CommandRunnable {
		@Override
		public void run(String[] args) throws Exception {
			if(args.length == 0) {
				System.out.println("No files passed as arguments.");
				printUsage(System.out);
				return;
			}
			final DatabaseService dbService = createApplicationContext().getBean(DatabaseService.class);
			dbService.importSets(args);
		}
	}
}
