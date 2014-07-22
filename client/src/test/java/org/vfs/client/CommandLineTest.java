package org.vfs.client;

import org.junit.Assert;
import org.junit.Test;
import org.vfs.client.network.MessageSender;
import org.vfs.client.network.NetworkManager;
import org.vfs.client.network.UserManager;
import org.vfs.core.network.protocol.User;

import java.io.*;

import static org.mockito.Mockito.*;

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

        User user = new User("0", "nikita");
        verify(networkManager, atLeastOnce()).openSocket("localhost", "4499");
        verify(userManager, atLeastOnce()).setUser(user);
        verify(messageSender, atLeastOnce()).send(user, "connect nikita");
    }

    @Test
    public void testConnectCommandAlreadyAuthorized() throws Exception {
        NetworkManager networkManager = mock(NetworkManager.class);
        UserManager userManager = mock(UserManager.class);
        MessageSender messageSender = mock(MessageSender.class);
        when(networkManager.getMessageSender()).thenReturn(messageSender);
        when(userManager.isAuthorized()).thenReturn(true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setErr(ps);

        CommandLine cmd = new CommandLine(userManager, networkManager);
        cmd.execute("connect localhost:4499 nikita");

        User user = new User("0", "nikita");
        verify(networkManager, never()).openSocket("localhost", "4499");
        verify(userManager, never()).setUser(user);
        verify(messageSender, never()).send(user, "connect nikita");
        String result = baos.toString();
        Assert.assertEquals("Validation error : You are already authorized!\r\n", result);
    }

    @Test
    public void testQuitCommandNotAuthorized() throws Exception {
        NetworkManager networkManager = mock(NetworkManager.class);
        UserManager userManager = mock(UserManager.class);
        MessageSender messageSender = mock(MessageSender.class);
        when(networkManager.getMessageSender()).thenReturn(messageSender);
        when(userManager.isAuthorized()).thenReturn(false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setErr(ps);

        CommandLine cmd = new CommandLine(userManager, networkManager);
        cmd.execute("quit");

        verify(networkManager, never()).getMessageSender();
        verify(userManager, never()).getUser();

        String result = baos.toString();
        Assert.assertEquals("Validation error : You are not authorized or connection was lost!\r\n", result);
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

    @Test
    public void testDefaultCommandNotAuthorized() throws Exception {
        NetworkManager networkManager = mock(NetworkManager.class);
        UserManager userManager = mock(UserManager.class);
        MessageSender messageSender = mock(MessageSender.class);
        when(networkManager.getMessageSender()).thenReturn(messageSender);
        when(userManager.isAuthorized()).thenReturn(false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setErr(ps);

        CommandLine cmd = new CommandLine(userManager, networkManager);
        cmd.execute("default");

        verify(networkManager, never()).getMessageSender();
        verify(userManager, never()).getUser();

        String result = baos.toString();
        Assert.assertEquals("Validation error : Please connect to the server!\r\n", result);
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
    }

}