package org.vfs.server.command.impl;

import org.vfs.server.model.TreeStructure;
import org.vfs.server.model.Context;

import java.util.HashMap;

/**
 * @author Lipatov Nikita
 */
class AbstractCommand extends TreeStructure
{
    protected String commandName;
    protected boolean isBroadcastCommand = false;

    public String getCommandName()
    {
        return commandName;
    }

    public void setCommandName(String commandName)
    {
        this.commandName = commandName;
    }

    public boolean isBroadcastCommand()
    {
        return isBroadcastCommand;
    }

    public void setBroadcastCommand(boolean isBroadcastCommand)
    {
        this.isBroadcastCommand = isBroadcastCommand;
    }

    public Context parse(String command, String args)
    {
        Context context = new Context();
        String dirName = args.substring(0, args.length());

        HashMap<String, String> keys = new HashMap<String, String>();
        keys.put("command", command);
        keys.put("arg1", dirName);

        context.setKeys(keys);
        return context;
    }

    protected Context twoArgs(String command, String args)
    {
        Context context = new Context();
        String arg1 = args.substring(0, args.indexOf(" "));
        String arg2 = args.substring(args.indexOf(" ") + 1, args.length()).trim();

        HashMap<String, String> keys = new HashMap<String, String>();
        keys.put("command", command);
        keys.put("arg1", arg1);
        keys.put("arg2", arg2);

        context.setKeys(keys);
        return context;
    }





}
