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

import java.io.IOException;

/**
 * @author Lipatov Nikita
 * TODO: fix Connection to the server was lost after quit
 */
public class QuitCommand extends AbstractCommand implements Command
{
    public static final String YOU_NOT_AUTHORIZED = "You are not authorized!";
    public static final String WRONG              = "Something wrong!";

    public QuitCommand()
    {
        this.commandName = "quit";
    }

    public void action(Context context)
    {
        try
        (
            Authorization authorization = Authorization.getInstance();
        )
        {
            if (authorization.isAuthorized())
            {
                User user = context.getUser();
                CommandValues commandValues = context.getCommandValues();

                RequestService requestService = new RequestService();
                Request request = requestService.create(user.getId(), user.getLogin(), commandValues.getCommand());
                String requestXml = requestService.toXml(request);

                if(requestXml == null)
                {
                    context.setErrorMessage(WRONG);
                    return;
                }

                ClientThread connection = authorization.getConnection();
                connection.flush(requestXml);

                authorization.setUser(null);
            }
            else
            {
                context.setMessage(YOU_NOT_AUTHORIZED);
            }
            context.setCommandWasExecuted(true);
        }
        catch(IOException error)
        {
            context.setErrorMessage(error.getLocalizedMessage());
            context.setCommandWasExecuted(false);
        }
    }
}
