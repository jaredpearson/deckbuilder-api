package deckbuilder.mtg.db;

import com.google.common.base.Strings;

/**
 * Parses a set of statements delimited by semicolons from a script file. All
 * single line comments, starting with --, are removed also.
 * @author jared.pearson
 */
public class ScriptParser {
	public String[] parseScript(String script) {
		//remove single line comments
		script = script.replaceAll("\\-\\-.*", "");
		
		//split the script into statements based on semicolon
		String[] statements = script.split(";");
		
		//remove whitespace around each statement
		for(int index = 0; index < statements.length; index++) {
			statements[index] = Strings.emptyToNull(statements[index].trim());
		}
		
		return statements;
	}
}
