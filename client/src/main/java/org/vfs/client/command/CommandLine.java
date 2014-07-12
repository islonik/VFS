package org.vfs.client.command;

import org.vfs.client.model.Authorization;
import org.vfs.client.network.ClientThread;
import org.vfs.core.command.AbstractCommandLine;
import org.vfs.core.command.Command;
import org.vfs.core.command.CommandParser;
import org.vfs.core.command.CommandValues;
import org.vfs.core.model.Context;
import org.vfs.core.network.protocol.Request;
import org.vfs.core.network.protocol.RequestService;
import org.vfs.core.network.protocol.User;

import java.util.HashMap;

/**
 * @author Lipatov Nikita
 */
public class CommandLine extends AbstractCommandLine
{

    public static final String WRONG          = "Something wrong!";
    public static final String CONNECT_SERVER = "Please connect to the server.";

    private ClientMapping clientMapping = new ClientMapping();

    public Context execute(User user, String args)
    {
        args = trimSlashes(removeDoubleSlashes(args.toLowerCase().trim()));
        CommandParser parser = new CommandParser();
        parser.parse(args);

        CommandValues commandValues = parser.getCommandValues();

        Context context = new Context();
        context.setUser(user);
        context.setCommandValues(commandValues);

        // connect, quit and exit commands
        HashMap<String, Command> mapping = clientMapping.getMapping();
        if(mapping.containsKey(commandValues.getCommand()))
        {
            Command command = mapping.get(commandValues.getCommand());
            command.action(context);
        }
        // other commands
        else
        {
            Authorization authorization = Authorization.getInstance();
            if(authorization.isAuthorized())
            {
                ClientThread clientThread = authorization.getConnection();
                if (clientThread != null && clientThread.isConnected())
                {
                    RequestService requestService = new RequestService();
                    Request request = requestService.create(user.getId(), user.getLogin(), args);
                    String requestXml = requestService.toXml(request);
                    if (requestXml == null)
                    {
                        context.setErrorMessage(WRONG);
                    }
                    else
                    {
                        clientThread.flush(requestXml);
                        context.setCommandWasExecuted(true);
                    }
                }
            }
            else
            {
                context.setErrorMessage(CONNECT_SERVER);
            }
        }

        return context;
    }
}
