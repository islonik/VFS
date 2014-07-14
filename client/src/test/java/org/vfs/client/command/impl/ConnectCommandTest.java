package org.vfs.client.command.impl;

import org.junit.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.vfs.client.network.ClientConnection;
import org.vfs.client.network.ClientConnectionManager;
import org.vfs.core.model.Context;
import org.vfs.core.network.protocol.User;

import java.io.IOException;

/**
 * @author Lipatov Nikita
 */
public class ConnectCommandTest
{

    @Test
    public void testConnectCommand_validationCommand_testCase01() throws IOException
    {
        User user = new User();
        user.setId("12");
        user.setLogin("nikita");

        Context context = new Context();
        context.setUser(user);
        context.setCommand("connect localhost:4499");

        ConnectCommand connectCommand = new ConnectCommand();
        connectCommand.action(context);

        Assert.assertEquals(ConnectCommand.VALIDATION_MESSAGE, context.getMessage());
    }

    @Test
    public void testClientCommand_validationCommand_testCase02() throws IOException
    {
        User user = new User();
        user.setId("12");
        user.setLogin("nikita");

        Context context = new Context();
        context.setUser(user);
        context.setCommand("connect localhost nikita");

        ConnectCommand connectCommand = new ConnectCommand();
        connectCommand.action(context);

        Assert.assertEquals(ConnectCommand.VALIDATION_MESSAGE, context.getMessage());
    }

    @Test
    public void testClientCommand_general() throws IOException
    {
        Context context = new Context();
        context.setCommand("connect localhost:4499 nikita");

        ClientConnectionManager.setInstance(Mockito.mock(ClientConnectionManager.class));
        ClientConnection connection = Mockito.mock(ClientConnection.class);
        Mockito.when(ClientConnectionManager.getInstance().createClientConnection("localhost", "4499")).thenReturn(connection);
        Mockito.when(connection.isConnected()).thenReturn(true);

        ConnectCommand connectCommand = new ConnectCommand();
        connectCommand.action(context);

        Mockito.verify(connection).sendMessageToServer
        (
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<request>\n" +
            "    <user>\n" +
            "        <id>0</id>\n" +
            "        <login>nikita</login>\n" +
            "    </user>\n" +
            "    <command>connect nikita</command>\n" +
            "</request>\n"
        );

    }
}
