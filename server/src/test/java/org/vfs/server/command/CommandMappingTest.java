package org.vfs.server.command;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

/**
 * User: Lipatov Nikita
 */
public class CommandMappingTest
{

    @Test
    public void testGeneral()
    {
        HashMap<String, Command> mapping = CommandMapping.getCommandMapping();
        Assert.assertEquals("cd",      mapping.get("cd").getCommandName());
        Assert.assertEquals("connect", mapping.get("connect").getCommandName());
        Assert.assertEquals("copy",    mapping.get("copy").getCommandName());
        Assert.assertEquals("lock",    mapping.get("lock").getCommandName());
        Assert.assertEquals("mkdir",   mapping.get("mkdir").getCommandName());
        Assert.assertEquals("mkfile",  mapping.get("mkfile").getCommandName());
        Assert.assertEquals("move",    mapping.get("move").getCommandName());
        Assert.assertEquals("print",   mapping.get("print").getCommandName());
        Assert.assertEquals("quit",    mapping.get("quit").getCommandName());
        Assert.assertEquals("rm",      mapping.get("rm").getCommandName());
        Assert.assertEquals("unlock",  mapping.get("unlock").getCommandName());
    }
}
