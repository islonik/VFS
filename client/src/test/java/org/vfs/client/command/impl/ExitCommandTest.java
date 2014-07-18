package org.vfs.client.command.impl;

import org.junit.Assert;
import org.junit.Test;
import org.vfs.core.model.Context;
import org.vfs.core.network.protocol.User;

/**
 * @author Lipatov Nikita
 */
public class ExitCommandTest
{

    @Test
    public void testExitCommandTest_authorized()
    {
        User user = new User();
        user.setId("12");
        user.setLogin("nikita");

        Context context = new Context();
        context.setUser(user);

        ExitCommand command = new ExitCommand();
        command.action(context);

        Assert.assertTrue(context.isCommandWasExecuted());
        Assert.assertFalse(context.isExit());
        Assert.assertEquals(ExitCommand.TYPE_QUIT_COMMAND, context.getMessage());
    }

    @Test
    public void testExitCommandTest_not_authorized()
    {

        Context context = new Context();

        ExitCommand command = new ExitCommand();
        command.action(context);

        Assert.assertTrue(context.isCommandWasExecuted());
        Assert.assertTrue(context.isExit());
    }
}
