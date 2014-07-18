package org.vfs.client.command.impl;

import org.vfs.client.network.NetworkManager;
import org.vfs.core.command.AbstractCommand;
import org.vfs.core.command.Command;
import org.vfs.core.command.CommandValues;
import org.vfs.core.model.Context;
import org.vfs.core.network.protocol.*;

/**
 * @author Lipatov Nikita
 */
public class QuitCommand extends AbstractCommand implements Command
{
    public static final String YOU_NOT_AUTHORIZED = "You are not authorized or connection was lost!";

    public QuitCommand()
    {
        this.commandName = "quit";
    }

    public void action(Context context)
    {
        User user = context.getUser();
        NetworkManager networkManager = NetworkManager.getInstance();

        if (user != null)
        {
            CommandValues commandValues = context.getCommandValues();

            boolean isSent = networkManager.getMessageSender().add(commandValues.getCommand());
            context.setCommandWasExecuted(isSent);
        }
        else
        {
            context.setMessage(YOU_NOT_AUTHORIZED);
        }
    }
}
