package org.vfs.client;

import org.junit.Test;
import static org.mockito.Mockito.*;
import org.vfs.client.network.MessageSender;
import org.vfs.client.network.NetworkManager;
import org.vfs.client.network.UserManager;
import org.vfs.core.VFSConstants;
import org.vfs.core.command.CommandParser;

import org.vfs.core.network.protocol.Protocol;

/**
 * @author Lipatov Nikita
 */
public class CommandLineTest {


    @Test
    public void testConnectCommand() throws Exception {
        NetworkManager networkManager = mock(NetworkManager.class);
        UserManager userManager = mock(UserManager.class);
        MessageSender messageSender = mock(MessageSender.class);
        when(networkManager.getMessageSender()).thenReturn(messageSender);
        when(userManager.isAuthorized()).thenReturn(false);

        CommandLine cmd = new CommandLine(userManager, networkManager);
        cmd.execute("connect localhost:4499 nikita");

        Protocol.User user = Protocol.User.newBuilder()
                .setId(VFSConstants.NEW_USER)
                .setLogin("nikita")
                .build();
        verify(networkManager, atLeastOnce()).openSocket("localhost", "4499");
        verify(userManager, atLeastOnce()).setUser(user);
        verify(messageSender, atLeastOnce()).send(user, "connect nikita");
    }

    @Test(expected = RuntimeException.class)
    public void testConnectCommandAlreadyAuthorized() {
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
    public void testQuitCommandNotAuthorized() {
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
    public void testQuitCommand() {
        NetworkManager networkManager = mock(NetworkManager.class);
        UserManager userManager = mock(UserManager.class);
        MessageSender messageSender = mock(MessageSender.class);
        when(networkManager.getMessageSender()).thenReturn(messageSender);
        when(userManager.isAuthorized()).thenReturn(true);
        Protocol.User user = Protocol.User.newBuilder()
                .setId(VFSConstants.NEW_USER)
                .setLogin("nikita")
                .build();
        when(userManager.getUser()).thenReturn(user);

        CommandLine cmd = new CommandLine(userManager, networkManager);
        cmd.execute("quit");

        verify(networkManager, atLeastOnce()).getMessageSender();
        verify(userManager, atLeastOnce()).getUser();
        verify(messageSender, atLeastOnce()).send(user, "quit");
    }

    @Test(expected = RuntimeException.class)
    public void testDefaultCommandNotAuthorized() {
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
    public void testDefaultCommand() {
        NetworkManager networkManager = mock(NetworkManager.class);
        UserManager userManager = mock(UserManager.class);
        MessageSender messageSender = mock(MessageSender.class);
        when(networkManager.getMessageSender()).thenReturn(messageSender);
        when(userManager.isAuthorized()).thenReturn(true);
        Protocol.User user = Protocol.User.newBuilder()
                .setId(VFSConstants.NEW_USER)
                .setLogin("nikita")
                .build();
        when(userManager.getUser()).thenReturn(user);

        CommandLine cmd = new CommandLine(userManager, networkManager);
        cmd.execute("default");

        verify(networkManager, atLeastOnce()).getMessageSender();
        verify(userManager, atLeastOnce()).getUser();
        verify(messageSender, atLeastOnce()).send(user, "default");
    }

}