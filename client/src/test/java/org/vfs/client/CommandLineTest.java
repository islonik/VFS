package org.vfs.client;

/**
 * @author Lipatov Nikita
 */
public class CommandLineTest {

    /*@Test
    public void testConnectCommand() throws Exception {
        NetworkManager networkManager = mock(NetworkManager.class);
        UserManager userManager = mock(UserManager.class);
        MessageSender messageSender = mock(MessageSender.class);
        when(networkManager.getMessageSender()).thenReturn(messageSender);
        when(userManager.isAuthorized()).thenReturn(false);

        CommandLine cmd = new CommandLine(userManager, networkManager);
        cmd.execute("connect localhost:4499 nikita");

        User user = new User("0", "nikita");
        verify(networkManager, atLeastOnce()).openSocket("localhost", "4499");
        verify(userManager, atLeastOnce()).setUser(user);
        verify(messageSender, atLeastOnce()).send(user, "connect nikita");
    }

    @Test(expected = RuntimeException.class)
    public void testConnectCommandAlreadyAuthorized() throws Exception {
        NetworkManager networkManager = mock(NetworkManager.class);
        UserManager userManager = mock(UserManager.class);
        MessageSender messageSender = mock(MessageSender.class);
        when(networkManager.getMessageSender()).thenReturn(messageSender);
        when(userManager.isAuthorized()).thenReturn(true);

        CommandLine cmd = new CommandLine(userManager, networkManager);
        CommandParser parser = new CommandParser();
        parser.parse("connect localhost:4499 nikita");
        cmd.commandValues = parser.getCommandValues();
        cmd.commands.get("connect").run();
    }

    @Test(expected = RuntimeException.class)
    public void testQuitCommandNotAuthorized() throws Exception {
        NetworkManager networkManager = mock(NetworkManager.class);
        UserManager userManager = mock(UserManager.class);
        MessageSender messageSender = mock(MessageSender.class);
        when(networkManager.getMessageSender()).thenReturn(messageSender);
        when(userManager.isAuthorized()).thenReturn(false);

        CommandLine cmd = new CommandLine(userManager, networkManager);
        CommandParser parser = new CommandParser();
        parser.parse("quit");
        cmd.commandValues = parser.getCommandValues();
        cmd.commands.get("quit").run();
    }

    @Test
    public void testQuitCommand() throws Exception {
        NetworkManager networkManager = mock(NetworkManager.class);
        UserManager userManager = mock(UserManager.class);
        MessageSender messageSender = mock(MessageSender.class);
        when(networkManager.getMessageSender()).thenReturn(messageSender);
        when(userManager.isAuthorized()).thenReturn(true);
        User user = new User("0", "nikita");
        when(userManager.getUser()).thenReturn(user);

        CommandLine cmd = new CommandLine(userManager, networkManager);
        cmd.execute("quit");

        verify(networkManager, atLeastOnce()).getMessageSender();
        verify(userManager, atLeastOnce()).getUser();
        verify(messageSender, atLeastOnce()).send(user, "quit");
    }

    @Test(expected = RuntimeException.class)
    public void testDefaultCommandNotAuthorized() throws Exception {
        NetworkManager networkManager = mock(NetworkManager.class);
        UserManager userManager = mock(UserManager.class);
        MessageSender messageSender = mock(MessageSender.class);
        when(networkManager.getMessageSender()).thenReturn(messageSender);
        when(userManager.isAuthorized()).thenReturn(false);

        CommandLine cmd = new CommandLine(userManager, networkManager);
        CommandParser parser = new CommandParser();
        parser.parse("default");
        cmd.commandValues = parser.getCommandValues();
        cmd.commands.get("default").run();
    }

    @Test
    public void testDefaultCommand() throws Exception {
        NetworkManager networkManager = mock(NetworkManager.class);
        UserManager userManager = mock(UserManager.class);
        MessageSender messageSender = mock(MessageSender.class);
        when(networkManager.getMessageSender()).thenReturn(messageSender);
        when(userManager.isAuthorized()).thenReturn(true);
        User user = new User("0", "nikita");
        when(userManager.getUser()).thenReturn(user);

        CommandLine cmd = new CommandLine(userManager, networkManager);
        cmd.execute("default");

        verify(networkManager, atLeastOnce()).getMessageSender();
        verify(userManager, atLeastOnce()).getUser();
        verify(messageSender, atLeastOnce()).send(user, "default");
    }*/

}