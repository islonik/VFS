package org.vfs.core.model;

import org.junit.Assert;
import org.junit.Test;
import org.vfs.core.network.protocol.User;
import org.vfs.core.command.CommandValues;

/**
 * @author Lipatov Nikita
 */
public class ContextTest
{
    @Test
    public void testSimpleTest()
    {
        User user = new User();
        user.setLogin("nikita");

        CommandValues values = new CommandValues();
        values.setCommand("mkdir");
        values.getParams().add("testDir2");
        Context context = new Context(user);
        context.setCommandValues(values);

        values = context.getCommandValues();

        Assert.assertEquals("mkdir",    values.getCommand());
        Assert.assertEquals("testDir2", values.getNextParam());
        Assert.assertNull(values.getNextParam());
        Assert.assertNull(values.getNextParam());
    }
}
