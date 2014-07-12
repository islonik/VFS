package org.vfs.core.command;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Lipatov Nikita
 */
public class CommandParserTest
{
    @Test
    public void testClientCommandParse_case01()
    {
        CommandParser parser = new CommandParser();

        String connectCommand = "connect WSM3-479:4499 admin";
        parser.parse(connectCommand);
        CommandValues values = parser.getCommandValues();

        Assert.assertEquals("connect",   values.getCommand());
        Assert.assertEquals("WSM3-479",  values.getNextParam());
        Assert.assertEquals("4499",      values.getNextParam());
        Assert.assertEquals("admin",     values.getNextParam());
    }

    @Test
    public void testClientCommandParse_case02()
    {
        CommandParser parser = new CommandParser();

        String connectCommand = "connect 192.168.0.1:5999 nikita";
        parser.parse(connectCommand);
        CommandValues values = parser.getCommandValues();

        Assert.assertEquals("connect",     values.getCommand());
        Assert.assertEquals("192.168.0.1", values.getNextParam());
        Assert.assertEquals("5999",        values.getNextParam());
        Assert.assertEquals("nikita",      values.getNextParam());
    }

    @Test
    public void testClientQuitCommandParse()
    {
        CommandParser parser = new CommandParser();

        String quitCommand = "quit";
        parser.parse(quitCommand);
        CommandValues values = parser.getCommandValues();

        Assert.assertEquals("quit", values.getCommand());
    }

    @Test
    public void testClientExitCommandParse()
    {
        CommandParser parser = new CommandParser();

        String exitCommand = "exit";
        parser.parse(exitCommand);
        CommandValues values = parser.getCommandValues();

        Assert.assertEquals("exit", values.getCommand());
    }

    @Test
    public void testClientEmptyCommandParse()
    {
        CommandParser parser = new CommandParser();

        String emptyCommand = "";
        parser.parse(emptyCommand);
        CommandValues values = parser.getCommandValues();

        Assert.assertEquals(null, values.getCommand());
    }
}
