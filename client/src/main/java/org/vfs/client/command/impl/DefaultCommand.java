package org.vfs.client.command.impl;

import org.vfs.client.network.ClientConnection;
import org.vfs.client.network.ClientConnectionManager;
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

        ClientConnectionManager clientConnectionManager = ClientConnectionManager.getInstance();
        ClientConnection clientConnection = clientConnectionManager.getClientConnection();

        if(user != null && clientConnection.isConnected())
        {
            // create request and send it to server
            RequestService requestService = new RequestService();
            Request request = requestService.create(user.getId(), user.getLogin(), command);
            String requestXml = requestService.toXml(request);
            clientConnection.sendMessageToServer(requestXml);

            context.setCommandWasExecuted(true);
        }
        else
        {
            context.setErrorMessage(CONNECT_SERVER);
        }

    }
}
