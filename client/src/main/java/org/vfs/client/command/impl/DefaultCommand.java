package org.vfs.client.command.impl;

import org.vfs.client.network.NetworkManager;
import org.vfs.core.command.AbstractCommand;
import org.vfs.core.command.Command;
import org.vfs.core.model.Context;
import org.vfs.core.network.protocol.*;

/**
 * @author Lipatov Nikita
 */
public class DefaultCommand extends AbstractCommand implements Command
{
    public static final String CONNECT_SERVER = "Please connect to the server.";

    public DefaultCommand()
    {
        this.commandName = "default";
    }

    public void action(Context context)
    {
        User user = context.getUser();
        String command = context.getCommand();

        NetworkManager networkManager = NetworkManager.getInstance();

        if(user != null )
        {
            boolean isSent = networkManager.getMessageSender().add(command);
            context.setCommandWasExecuted(isSent);
        }
        else
        {
            context.setErrorMessage(CONNECT_SERVER);
        }
    }
}
