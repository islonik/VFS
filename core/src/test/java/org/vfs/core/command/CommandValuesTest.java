package org.vfs.core.command;

import org.junit.Assert;
import org.junit.Test;
import org.vfs.core.command.CommandValues;

/**
 * @author Lipatov Nikita
 */
public class CommandValuesTest
{

    @Test
    public void test()
    {
        CommandValues values = new CommandValues();
        values.setCommand("copy");
        values.getParams().add("test");
        values.getParams().add("test2");

        Assert.assertNull(values.getNextKey());
        Assert.assertEquals("copy", values.getCommand());
        Assert.assertEquals("test", values.getNextParam());
        Assert.assertEquals("test2", values.getNextParam());
        Assert.assertNull(values.getNextParam());
    }
}
