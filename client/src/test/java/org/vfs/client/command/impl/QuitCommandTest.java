package org.vfs.client.command.impl;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.vfs.client.network.ClientConnection;
import org.vfs.client.network.ClientConnectionManager;
import org.vfs.core.model.Context;
import org.vfs.core.network.protocol.User;

import java.io.IOException;

/**
 * @author Lipatov Nikita
 */
public class QuitCommandTest
{

    @Test
    public void testQuitCommandTest_authorized()
    {
        Context context = new Context();

        QuitCommand command = new QuitCommand();
        command.action(context);

        Assert.assertEquals(QuitCommand.YOU_NOT_AUTHORIZED, context.getMessage());
        Assert.assertFalse(context.isExit());
        Assert.assertTrue(context.isCommandWasExecuted());
    }

    @Test
    public void testQuitCommandTest_not_authorized() throws IOException
    {
        User user = new User();
        user.setId("344");
        user.setLogin("user");

        Context context = new Context();
        context.setUser(user);
        context.setCommand("quit");

        ClientConnectionManager.setInstance(Mockito.mock(ClientConnectionManager.class));
        ClientConnection connection = Mockito.mock(ClientConnection.class);
        Mockito.when(ClientConnectionManager.getInstance().getClientConnection()).thenReturn(connection);
        Mockito.when(connection.isConnected()).thenReturn(true);

        QuitCommand command = new QuitCommand();
        command.action(context);

        Assert.assertFalse(context.isExit());
        Assert.assertTrue(context.isCommandWasExecuted());

        Mockito.verify(connection).sendMessageToServer
        (
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<request>\n" +
            "    <user>\n" +
            "        <id>344</id>\n" +
            "        <login>user</login>\n" +
            "    </user>\n" +
            "    <command>quit</command>\n" +
            "</request>\n"
        );
    }

}
