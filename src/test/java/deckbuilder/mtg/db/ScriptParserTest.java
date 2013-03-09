package deckbuilder.mtg.db;

import org.junit.Assert;
import org.junit.Test;

public class ScriptParserTest {

	/**
	 * Given one statement without comments the statement should be returned fully
	 */
	@Test
	public void testParseWithOneStatementNoComments() {
		String script = "create table test(id not null)";
		
		ScriptParser parser = new ScriptParser();
		String[] actual = parser.parseScript(script);
		
		Assert.assertNotNull("Parser should not return null when a script is present", actual);
		Assert.assertEquals("Expected the parser to return the single statement from the script", 1, actual.length);
		Assert.assertEquals("Expected the parser to return the same statement as entered", script, actual[0]);
	}
	
	/**
	 * Given one statement with a trailing semicolon
	 */
	@Test
	public void testParseOneStatementWithTrailingSemicolon() {
		String script = "create table test(id not null);";
		
		ScriptParser parser = new ScriptParser();
		String[] actual = parser.parseScript(script);
		
		Assert.assertNotNull("Parser should not return null when a script is present", actual);
		Assert.assertEquals("Expected the parser to return the single statement from the script", 1, actual.length);
		Assert.assertEquals("Expected the parser to return the statement without the semicolon", "create table test(id not null)", actual[0]);
	}

	/**
	 * Test one statement with comments before
	 */
	@Test
	public void testParseWithOneStatementCommentsBefore() {
		String script = "--comment\ncreate table test(id not null)";
		
		ScriptParser parser = new ScriptParser();
		String[] actual = parser.parseScript(script);
		
		Assert.assertNotNull("Parser should not return null when a script is present", actual);
		Assert.assertEquals("Expected the parser to return the single statement from the script", 1, actual.length);
		Assert.assertEquals("Expected the parser to return the same statement without the comment", "create table test(id not null)", actual[0]);
	}
	
	/**
	 * Test one statement with comments after
	 */
	@Test
	public void testParseWithOneStatementCommentsAfter() {
		String script = "create table test(id not null)\n--comment";
		
		ScriptParser parser = new ScriptParser();
		String[] actual = parser.parseScript(script);
		
		Assert.assertNotNull("Parser should not return null when a script is present", actual);
		Assert.assertEquals("Expected the parser to return the single statement from the script", 1, actual.length);
		Assert.assertEquals("Expected the parser to return the same statement without the comment", "create table test(id not null)", actual[0]);
	}

	/**
	 * Test one statement with new lines, tabs and spaces
	 */
	@Test
	public void testParseWithOneStatementWhitespace() {
		String script = "\n\n  \t\n  create table test(id not null)\n\n  \t\n";
		
		ScriptParser parser = new ScriptParser();
		String[] actual = parser.parseScript(script);
		
		Assert.assertNotNull("Parser should not return null when a script is present", actual);
		Assert.assertEquals("Expected the parser to return the single statement from the script", 1, actual.length);
		Assert.assertEquals("Expected the parser to return the same statement without the whitespace", "create table test(id not null)", actual[0]);
	}

	/**
	 * Given multiple statements without comments the statement should be returned fully
	 */
	@Test
	public void testParseWithMultipleStatementsNoComments() {
		String script = "create table test(id integer not null, name varchar(20));create index name_idx on test(name)";
		
		ScriptParser parser = new ScriptParser();
		String[] actual = parser.parseScript(script);
		
		Assert.assertNotNull("Parser should not return null when a script is present", actual);
		Assert.assertEquals("Expected the parser to return the two statement from the script", 2, actual.length);
		Assert.assertEquals("Expected the parser to return the first statement", "create table test(id integer not null, name varchar(20))", actual[0]);
		Assert.assertEquals("Expected the parser to return the second statement", "create index name_idx on test(name)", actual[1]);
	}

	/**
	 * Test mutiple statements with comments before
	 */
	@Test
	public void testParseWithMultipleStatementsCommentsBefore() {
		String script = "--comment\ncreate table test(id integer not null, name varchar(20));create index name_idx on test(name)";
		
		ScriptParser parser = new ScriptParser();
		String[] actual = parser.parseScript(script);
		
		Assert.assertNotNull("Parser should not return null when a script is present", actual);
		Assert.assertEquals("Expected the parser to return the two statement from the script", 2, actual.length);
		Assert.assertEquals("Expected the parser to return the first statement", "create table test(id integer not null, name varchar(20))", actual[0]);
		Assert.assertEquals("Expected the parser to return the second statement", "create index name_idx on test(name)", actual[1]);
	}
	
	/**
	 * Test multiple statements with comments after
	 */
	@Test
	public void testParseWithMultipleStatementsCommentsAfter() {
		String script = "create table test(id integer not null, name varchar(20));create index name_idx on test(name)\n--comment";
		
		ScriptParser parser = new ScriptParser();
		String[] actual = parser.parseScript(script);
		
		Assert.assertNotNull("Parser should not return null when a script is present", actual);
		Assert.assertEquals("Expected the parser to return the two statements from the script", 2, actual.length);
		Assert.assertEquals("Expected the parser to return the first statement", "create table test(id integer not null, name varchar(20))", actual[0]);
		Assert.assertEquals("Expected the parser to return the second statement", "create index name_idx on test(name)", actual[1]);
	}

	/**
	 * Test multiple statements with new lines, tabs and spaces
	 */
	@Test
	public void testParseWithMultipleStatementsWithWhitespace() {
		String script = "\n\n  \t\n  create table test(id integer not null, name varchar(20));  \n \t \n  create index name_idx on test(name)\n\n  \t\n";
		
		ScriptParser parser = new ScriptParser();
		String[] actual = parser.parseScript(script);
		
		Assert.assertNotNull("Parser should not return null when a script is present", actual);
		Assert.assertEquals("Expected the parser to return the two statements from the script", 2, actual.length);
		Assert.assertEquals("Expected the parser to return the first statement", "create table test(id integer not null, name varchar(20))", actual[0]);
		Assert.assertEquals("Expected the parser to return the second statement", "create index name_idx on test(name)", actual[1]);
	}
	
	/**
	 * Test statement with inline comments
	 */
	@Test
	public void testParseStatementWithInlineComments() {
		String script = "create table test(id integer not null, -- this is a comment\n name varchar(20))";
		
		ScriptParser parser = new ScriptParser();
		String[] actual = parser.parseScript(script);
		
		Assert.assertNotNull("Parser should not return null when a script is present", actual);
		Assert.assertEquals("Expected the parser to return the one statement from the script", 1, actual.length);
		Assert.assertEquals("Expected the parser to return the first statement", "create table test(id integer not null, \n name varchar(20))", actual[0]);
	}
}
