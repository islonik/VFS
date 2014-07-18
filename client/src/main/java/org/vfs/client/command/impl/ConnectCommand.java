package org.vfs.client.command.impl;

import org.vfs.client.model.UserManager;
import org.vfs.client.network.NetworkManager;
import org.vfs.core.command.AbstractCommand;
import org.vfs.core.command.Command;
import org.vfs.core.command.CommandValues;
import org.vfs.core.model.Context;
import org.vfs.core.network.protocol.*;

import java.io.IOException;

/**
 * @author Lipatov Nikita
 */
public class ConnectCommand extends AbstractCommand implements Command
{
    public static final String VALIDATION_MESSAGE = "ServerHost or ServerPort or UserLogin doesn't found!";
    public static final String YOU_ALREADY_AUTHORIZED = "You are already authorized!";
    public static final String CONNECTION_NOT_ESTABLISHED = "Connection wasn't established! Please check host name and port!";

    public ConnectCommand()
    {
        this.commandName = "connect";
    }

    public void action(Context context)
    {
        CommandValues commandValues = context.getCommandValues();
        User user = context.getUser();

        String serverHost = commandValues.getNextParam();
        String serverPort = commandValues.getNextParam();
        String userLogin  = commandValues.getNextParam();

        if(serverHost == null || serverPort == null || userLogin == null)
        {
            context.setErrorMessage(VALIDATION_MESSAGE);
            return;
        }

        if(user != null)
        {
            context.setMessage(YOU_ALREADY_AUTHORIZED);
            context.setCommandWasExecuted(true);
            return;
        }

        try
        {
            NetworkManager networkManager = NetworkManager.getInstance();
            networkManager.createServerConnection(serverHost, serverPort);

            // connection was established
            user = new User();
            user.setId("0");
            user.setLogin(userLogin);

            UserManager userManager = UserManager.getInstance();
            userManager.setUser(user);

            // create request and send it to server
            networkManager.getMessageSender().add("connect " + user.getLogin());

            context.setCommandWasExecuted(true);
        }
        catch (IOException ioe)
        {
            context.setErrorMessage(ioe.getLocalizedMessage());
        }
    }
}