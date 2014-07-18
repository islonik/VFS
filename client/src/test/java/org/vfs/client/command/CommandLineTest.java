package org.vfs.client.command;

import org.junit.Assert;
import org.junit.Test;
import org.vfs.client.command.impl.DefaultCommand;
import org.vfs.core.model.Context;
import org.vfs.core.network.protocol.User;

import org.mockito.Mockito;

/**
 *
 * @author Lipatov Nikita
 */
public class CommandLineTest
{

    @Test
    public void testCommandLine_not_authorized()
    {
        CommandLine cmd = new CommandLine();
        Context context = cmd.execute(null, "print");

        Assert.assertEquals(DefaultCommand.CONNECT_SERVER, context.getMessage());
        Assert.assertFalse(context.isCommandWasExecuted());
    }

    @Test
    public void testCommandLine_other_commands()
    {
        User user = new User();
        user.setId("12");
        user.setLogin("nikita");

        /*NetworkManager.setInstance(Mockito.mock(NetworkManager.class));
        ClientConnection connection = Mockito.mock(ClientConnection.class);
        //Mockito.when(NetworkManager.getInstance().getClientConnection()).thenReturn(connection);
        Mockito.when(connection.isConnected()).thenReturn(true);

        CommandLine cmd = new CommandLine();
        Context context = cmd.execute(user, "anycommand anyparam1 anyparam2");

        Assert.assertTrue(context.isCommandWasExecuted());
        Mockito.verify(connection).sendMessageToServer
        (
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<request>\n" +
            "    <user>\n" +
            "        <id>12</id>\n" +
            "        <login>nikita</login>\n" +
            "    </user>\n" +
            "    <command>anycommand anyparam1 anyparam2</command>\n" +
            "</request>\n"
        ); */
    }




}