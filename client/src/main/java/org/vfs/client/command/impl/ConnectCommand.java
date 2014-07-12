package org.vfs.client.command.impl;

import org.vfs.client.model.Authorization;
import org.vfs.client.network.ClientThread;
import org.vfs.core.command.AbstractCommand;
import org.vfs.core.command.Command;
import org.vfs.core.command.CommandValues;
import org.vfs.core.model.Context;
import org.vfs.core.network.protocol.Request;
import org.vfs.core.network.protocol.RequestService;
import org.vfs.core.network.protocol.User;

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

        String serverHost = commandValues.getNextParam();
        String serverPort = commandValues.getNextParam();
        String userLogin  = commandValues.getNextParam();

        if(serverHost == null || serverPort == null || userLogin == null)
        {
            context.setErrorMessage(VALIDATION_MESSAGE);
            return;
        }

        if(Authorization.getInstance().isAuthorized())
        {
            context.setMessage(YOU_ALREADY_AUTHORIZED);
            context.setCommandWasExecuted(true);
            return;
        }

        // attempt of establishing the connection to the server
        ClientThread client = new ClientThread(serverHost, serverPort);

        if(!client.isConnected())
        {
            context.setErrorMessage(CONNECTION_NOT_ESTABLISHED);
            return;
        }

        // connection was established
        User user = new User();
        user.setId("0");
        user.setLogin(userLogin);

        Authorization.getInstance().setUser(user);
        Authorization.getInstance().setConnection(client);

        RequestService requestService = new RequestService();
        Request request = requestService.create(user.getId(), user.getLogin(), "connect " + user.getLogin());

        String requestXml = requestService.toXml(request);
        client.flush(requestXml); // first command
        context.setCommandWasExecuted(true);
    }
}
