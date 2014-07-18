package org.vfs.client.command.impl;

import org.vfs.core.command.AbstractCommand;
import org.vfs.core.command.Command;
import org.vfs.core.model.Context;
import org.vfs.core.network.protocol.User;

/**
 * @author Lipatov Nikita
 */
public class ExitCommand extends AbstractCommand implements Command
{
    public static final String TYPE_QUIT_COMMAND = "For a start you should type 'quit' command!";

    public ExitCommand()
    {
        this.commandName = "exit";
    }

    public void action(Context context)
    {
        User user = context.getUser();
        if(user != null)
        {
            context.setMessage(TYPE_QUIT_COMMAND);
        }
        else
        {
            context.setExit(true);
        }
        context.setCommandWasExecuted(true);
    }
}
