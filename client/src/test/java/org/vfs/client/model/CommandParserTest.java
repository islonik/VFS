package org.vfs.client.model;

import org.junit.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Lipatov Nikita
 */
public class CommandParserTest
{

    public CommandParserTest() {}

    @BeforeClass
    public static void setUpClass() throws Exception {}

    @AfterClass
    public static void tearDownClass() throws Exception {}

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    /**
     * Test of getPath method, of class CommandParser.
     */
    @Test
    public void testCommandParser_general_testCase01()
    {
        Authorization authorization = new Authorization();
        CommandParser parser = new CommandParser(authorization);

        String connectCommand = "connect WSM3-479:4499 admin";

        Assert.assertTrue(parser.isConnectCommand(connectCommand));
        Assert.assertFalse(parser.isQuitCommand(connectCommand));

        Assert.assertEquals("connect",   parser.getCommand(connectCommand));
        Assert.assertEquals("WSM3-479",  parser.getHost(connectCommand));
        Assert.assertEquals("4499",      parser.getPort(connectCommand));
        Assert.assertEquals("admin",     parser.getLogin(connectCommand));
    }

    @Test
    public void testCommandParser_general_testCase02()
    {
        Authorization authorization = new Authorization();
        CommandParser parser = new CommandParser(authorization);

        String connectCommand = "connect 192.168.0.1:5999 nikita";

        Assert.assertEquals("connect",     parser.getCommand(connectCommand));
        Assert.assertEquals("192.168.0.1", parser.getHost(connectCommand));
        Assert.assertEquals("5999",        parser.getPort(connectCommand));
        Assert.assertEquals("nikita",      parser.getLogin(connectCommand));
    }

    @Test
    public void testCommandParser_general_testCase03()
    {
        Authorization authorization = new Authorization();
        CommandParser parser = new CommandParser(authorization);

        String connectCommand = "connect 192.168.0.2 nikita";

        Assert.assertEquals("connect",     parser.getCommand(connectCommand));
        Assert.assertEquals("192.168.0.2", parser.getHost(connectCommand));
        Assert.assertEquals("4499",        parser.getPort(connectCommand));
        Assert.assertEquals("nikita",      parser.getLogin(connectCommand));
    }

    @Test(expected = RuntimeException.class)
    public void testCommandParser_testCase04()
    {
        Authorization authorization = new Authorization();
        CommandParser parser = new CommandParser(authorization);

        String connectCommand = "connect server";

        Assert.assertEquals("connect",     parser.getCommand(connectCommand));
        Assert.assertEquals("server",      parser.getHost(connectCommand));
        Assert.assertEquals("4499",        parser.getPort(connectCommand));
        parser.getLogin(connectCommand); // exception
    }

    @Test
    public void testCommandParser_general_testCase05()
    {
        Authorization authorization = new Authorization();
        CommandParser parser = new CommandParser(authorization);

        String connectCommand = "quit";

        Assert.assertFalse(parser.isConnectCommand(connectCommand));
        Assert.assertTrue(parser.isQuitCommand(connectCommand));
    }

    @Test
    public void testCommandParser_general_testCase06()
    {
        Authorization authorization = new Authorization();
        CommandParser parser = new CommandParser(authorization);

        String connectCommand = "exit";

        Assert.assertFalse(parser.isConnectCommand(connectCommand));
        Assert.assertFalse(parser.isQuitCommand(connectCommand));
        Assert.assertTrue(parser.isExitCommand(connectCommand));
    }

    @Test
    public void testCommandParser_general_testCase07()
    {
        Authorization authorization = new Authorization();
        CommandParser parser = new CommandParser(authorization);

        String connectCommand = "";

        Assert.assertFalse(parser.isConnectCommand(connectCommand));
        Assert.assertFalse(parser.isQuitCommand(connectCommand));
        Assert.assertFalse(parser.isExitCommand(connectCommand));
    }

    @Test
    public void testCommandParser_general_testCase08()
    {
        Authorization authorization = new Authorization();
        authorization.setUser(new User("344", "user"));
        Assert.assertTrue(authorization.isAuthorized());

        CommandParser parser = new CommandParser(authorization);

        String connectCommand = "connect user";

        String xml = parser.getXML(connectCommand);

        Assert.assertEquals
        (
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<request>\n" +
            "  <user id=\"344\" login=\"user\" />\n" +
            "  <command>connect user</command>\n" +
            "</request>",
            xml
        );
    }

    private static final String responseExample01 =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<response>\n" +
        "  <code>2</code>\n" +
        "  <message>/home/demon</message>\n" +
        "  <specific_code>139877254875232</specific_code>\n" +
        "</response>\n";

    @Test
    public void testCommandParser_response_testCase01()
    {
        Authorization authorization = new Authorization();
        authorization.setUser(new User(" ", "demon"));
        Assert.assertFalse(authorization.isAuthorized());

        CommandParser parser = new CommandParser(authorization);

        Assert.assertTrue(parser.parseServerResponse(responseExample01));
        Assert.assertTrue(authorization.isAuthorized());
    }

    private static final String responseExample02 =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<response>\n" +
            "  <code>3</code>\n" +
            "  <message>Such user already exist!</message>\n" +
            "</response>\n";

    @Test
    public void testCommandParser_response_testCase02()
    {
        Authorization authorization = new Authorization();
        authorization.setUser(new User(" ", "demon"));
        Assert.assertFalse(authorization.isAuthorized());

        CommandParser parser = new CommandParser(authorization);

        Assert.assertTrue(parser.parseServerResponse(responseExample02));
        Assert.assertFalse(authorization.isAuthorized());
    }

    private static final String responseExample03 =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<response>\n" +
        "  <code>4</code>\n" +
        "  <message>You are disconnected from server</message>\n" +
        "</response>\n";

    @Test
    public void testCommandParser_response_testCase03()
    {
        Authorization authorization = new Authorization();
        authorization.setUser(new User("12345", "demon"));
        Assert.assertTrue(authorization.isAuthorized());

        CommandParser parser = new CommandParser(authorization);

        Assert.assertFalse(parser.parseServerResponse(responseExample03));
        Assert.assertFalse(authorization.isAuthorized());
    }


    @Test
    public void testCommandParser_inputCommand_testCase01()
    {
        Authorization authorization = new Authorization();
        authorization.setUser(null);
        Assert.assertFalse(authorization.isAuthorized());

        CommandParser parser = new CommandParser(authorization);

        String inputString = "quit";

        Assert.assertTrue(parser.parserClientCommand(inputString));
        Assert.assertEquals(CommandParser.YOU_NOT_AUTHORIZED, parser.getOutMessage());
    }

    @Test
    public void testCommandParser_inputCommand_testCase02()
    {
        Authorization authorization = new Authorization();
        authorization.setUser(new User("33", "user"));
        Assert.assertTrue(authorization.isAuthorized());

        CommandParser parser = new CommandParser(authorization);

        String inputString = "exit";

        Assert.assertTrue(parser.parserClientCommand(inputString));
        Assert.assertEquals(CommandParser.TYPE_QUIT_COMMAND, parser.getOutMessage());
    }

    @Test
    public void testCommandParser_inputCommand_testCase03()
    {
        Authorization authorization = new Authorization();
        authorization.setUser(null);
        Assert.assertFalse(authorization.isAuthorized());

        CommandParser parser = new CommandParser(authorization);

        String inputString = "exit";

        Assert.assertFalse(parser.parserClientCommand(inputString));
    }

    @Test
    public void testCommandParser_inputCommand_testCase04()
    {

        Authorization authorization = new Authorization();
        authorization.setUser(new User("33", "user"));
        Assert.assertTrue(authorization.isAuthorized());

        CommandParser parser = new CommandParser(authorization);

        String inputString = "connect localhost:4499 nikita";

        Assert.assertTrue(parser.parserClientCommand(inputString));
        Assert.assertEquals(CommandParser.YOU_ALREADY_AUTHORIZED, parser.getOutMessage());
    }



}