package org.vfs.client.command;

import org.junit.Assert;
import org.junit.Test;
import org.vfs.client.command.impl.ConnectCommand;
import org.vfs.client.command.impl.ExitCommand;
import org.vfs.client.command.impl.QuitCommand;
import org.vfs.core.command.Command;

import java.util.HashMap;

/**
 * @author Lipatov Nikita
 */
public class ClientMappingTest
{

    @Test
    public void testGettingCommands()
    {
        ClientMapping clientMapping = new ClientMapping();
        HashMap<String, Command> commands = clientMapping.getMapping();

        Assert.assertEquals(ConnectCommand.class.getCanonicalName(),  commands.get("connect").getClass().getCanonicalName());
        Assert.assertEquals(QuitCommand.class.getCanonicalName(),     commands.get("quit").getClass().getCanonicalName());
        Assert.assertEquals(ExitCommand.class.getCanonicalName(),     commands.get("exit").getClass().getCanonicalName());
    }
}
