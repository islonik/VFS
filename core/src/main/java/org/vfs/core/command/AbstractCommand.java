package org.vfs.core.command;

/**
 * @author Lipatov Nikita
 */
public abstract class AbstractCommand
{
    protected String commandName;

    public String getCommandName()
    {
        return commandName;
    }

    public void setCommandName(String commandName)
    {
        this.commandName = commandName;
    }


}
