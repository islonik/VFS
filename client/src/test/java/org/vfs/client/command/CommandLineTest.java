package org.vfs.client.command;

import org.junit.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.vfs.client.command.impl.ConnectCommand;
import org.vfs.client.command.impl.ExitCommand;
import org.vfs.client.command.impl.QuitCommand;
import org.vfs.client.model.Authorization;
import org.vfs.client.network.ClientThread;
import org.vfs.core.model.Context;
import org.vfs.core.network.protocol.User;

import org.mockito.Mockito;

/**
 *
 * @author Lipatov Nikita
 */
public class CommandLineTest
{

    public CommandLineTest() {}

    @BeforeClass
    public static void setUpClass() throws Exception {}

    @AfterClass
    public static void tearDownClass() throws Exception {}

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    @Test
    public void testClientCommandLine_ExitCommand_noAuthorizedCase()
    {
        Authorization authorization = Authorization.newInstance();
        Assert.assertFalse(authorization.isAuthorized());

        CommandLine clientCommandLine = new CommandLine();
        Context context = clientCommandLine.execute(authorization.getUser(), "exit");

        Assert.assertTrue(context.isThreadClose());
        Assert.assertTrue(context.isCommandWasExecuted());

    }

    @Test
    public void testClientCommandLine_ExitCommand_authorizedCase()
    {
        Authorization authorization = Authorization.newInstance();

        User user = new User();
        user.setId("344");
        user.setLogin("user");
        authorization.setUser(user);
        Assert.assertTrue(authorization.isAuthorized());

        CommandLine clientCommandLine = new CommandLine();
        Context context = clientCommandLine.execute(authorization.getUser(), "exit");

        Assert.assertEquals(ExitCommand.TYPE_QUIT_COMMAND, context.getMessage());
        Assert.assertFalse(context.isThreadClose());
        Assert.assertTrue(context.isCommandWasExecuted());
    }

    @Test
    public void testClientCommandLine_QuitCommand_noAuthorizedCase()
    {
        Authorization authorization = Authorization.newInstance();
        Assert.assertFalse(authorization.isAuthorized());

        CommandLine clientCommandLine = new CommandLine();
        Context context = clientCommandLine.execute(authorization.getUser(), "quit");

        Assert.assertEquals(QuitCommand.YOU_NOT_AUTHORIZED, context.getMessage());
        Assert.assertFalse(context.isThreadClose());
        Assert.assertTrue(context.isCommandWasExecuted());
    }

    @Test
    public void testClientCommandLine_QuitCommand_authorizedCase()
    {
        Authorization authorization = Authorization.newInstance();

        User user = new User();
        user.setId("344");
        user.setLogin("user");
        authorization.setUser(user);
        Assert.assertTrue(authorization.isAuthorized());

        ClientThread clientThread = Mockito.mock(ClientThread.class);
        authorization.setConnection(clientThread);

        CommandLine clientCommandLine = new CommandLine();
        Context context = clientCommandLine.execute(authorization.getUser(), "quit");

        Assert.assertFalse(context.isThreadClose());
        Assert.assertTrue(context.isCommandWasExecuted());

        Mockito.verify(clientThread).flush
        (
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<request>\n" +
            "    <user>\n" +
            "        <id>344</id>\n" +
            "        <login>user</login>\n" +
            "    </user>\n" +
            "    <command>quit</command>\n" +
            "</request>"
        );
    }

    @Test
    public void testClientCommandLine_ConnectCommand_wrongCase()
    {
        Authorization authorization = Authorization.newInstance();

        User user = new User();
        user.setId("344");
        user.setLogin("user");
        authorization.setUser(user);
        Assert.assertTrue(authorization.isAuthorized());

        ClientThread clientThread = Mockito.mock(ClientThread.class);
        authorization.setConnection(clientThread);

        CommandLine clientCommandLine = new CommandLine();
        Context context = clientCommandLine.execute(authorization.getUser(), "connect localhost nikita");

        Assert.assertEquals(ConnectCommand.VALIDATION_MESSAGE, context.getMessage());
        Assert.assertFalse(context.isThreadClose());
        Assert.assertFalse(context.isCommandWasExecuted());
    }

    @Test
    public void testClientCommandLine_ConnectCommand_authorizedCase()
    {
        Authorization authorization = Authorization.newInstance();

        User user = new User();
        user.setId("344");
        user.setLogin("user");
        authorization.setUser(user);
        Assert.assertTrue(authorization.isAuthorized());

        ClientThread clientThread = Mockito.mock(ClientThread.class);
        authorization.setConnection(clientThread);

        CommandLine clientCommandLine = new CommandLine();
        Context context = clientCommandLine.execute(authorization.getUser(), "connect localhost:4499 nikita");

        Assert.assertEquals(ConnectCommand.YOU_ALREADY_AUTHORIZED, context.getMessage());
        Assert.assertFalse(context.isThreadClose());
        Assert.assertTrue(context.isCommandWasExecuted());
    }

    /*@Test
    public void testClientCommandLine_ConnectCommand_noAuthorizedCase()
    {
        Authorization authorization = Authorization.newInstance();
        Assert.assertFalse(authorization.isAuthorized());

        ClientThread clientThread = Mockito.mock(ClientThread.class);
        Mockito.when(clientThread.isConnected()).thenReturn(true);
        authorization.setConnection(clientThread);

        CommandLine clientCommandLine = new CommandLine();
        Context context = clientCommandLine.execute(authorization.getUser(), "connect localhost:4499 nikita");

        Assert.assertFalse(context.isThreadClose());
        Assert.assertTrue(context.isCommandWasExecuted());
    }      */



    /**
    private static final String responseExample01 =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<response>\n" +
        "  <code>2</code>\n" +
        "  <message>/home/demon</message>\n" +
        "  <specificCode>139877254875232</specificCode>\n" +
        "</response>\n";

    @Test
    public void testCommandParser_response_testCase01()
    {
        Authorization authorization = new Authorization();
        User user = new User();
        user.setId(" ");
        user.setLogin("demon");
        authorization.setUser(user);
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
        User user = new User();
        user.setId(" ");
        user.setLogin("demon");
        authorization.setUser(user);
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
        User user = new User();
        user.setId("12345");
        user.setLogin("demon");
        authorization.setUser(user);
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

        Assert.assertTrue(parser.parseClientCommand(inputString));
        Assert.assertEquals(CommandParser.YOU_NOT_AUTHORIZED, parser.getOutMessage());
    }

    @Test
    public void testCommandParser_inputCommand_testCase02()
    {
        Authorization authorization = new Authorization();
        User user = new User();
        user.setId("33");
        user.setLogin("user");
        authorization.setUser(user);
        Assert.assertTrue(authorization.isAuthorized());

        CommandParser parser = new CommandParser(authorization);

        String inputString = "exit";

        Assert.assertTrue(parser.parseClientCommand(inputString));
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

        Assert.assertFalse(parser.parseClientCommand(inputString));
    }

    @Test
    public void testCommandParser_inputCommand_testCase04()
    {

        Authorization authorization = new Authorization();
        User user = new User();
        user.setId("33");
        user.setLogin("user");
        authorization.setUser(user);
        Assert.assertTrue(authorization.isAuthorized());

        CommandParser parser = new CommandParser(authorization);

        String inputString = "connect localhost:4499 nikita";

        Assert.assertTrue(parser.parseClientCommand(inputString));
        Assert.assertEquals(CommandParser.YOU_ALREADY_AUTHORIZED, parser.getOutMessage());
    }      */



}