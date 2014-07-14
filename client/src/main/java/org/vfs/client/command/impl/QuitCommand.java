package org.vfs.client.command.impl;

import org.vfs.client.network.ClientConnection;
import org.vfs.client.network.ClientConnectionManager;
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

        ClientConnectionManager clientConnectionManager = ClientConnectionManager.getInstance();
        ClientConnection clientConnection = clientConnectionManager.getClientConnection();

        if (user != null && clientConnection.isConnected())
        {
            CommandValues commandValues = context.getCommandValues();

            // create request and send it to server
            RequestService requestService = new RequestService();
            Request request = requestService.create(user.getId(), user.getLogin(), commandValues.getCommand());
            String requestXml = requestService.toXml(request);
            clientConnection.sendMessageToServer(requestXml);
        }
        else
        {
            context.setMessage(YOU_NOT_AUTHORIZED);
        }
        context.setCommandWasExecuted(true);

    }
}
