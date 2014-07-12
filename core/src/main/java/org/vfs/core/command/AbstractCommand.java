package org.vfs.core.command;

/**
 * @author Lipatov Nikita
 */
public abstract class AbstractCommand
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

}
