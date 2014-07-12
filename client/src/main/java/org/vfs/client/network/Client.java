package org.vfs.client.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vfs.client.command.CommandLine;
import org.vfs.client.model.Authorization;
import org.vfs.core.model.Context;
import org.vfs.core.network.protocol.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Lipatov Nikita
 */
public class Client 
{
    private static final Logger log = LoggerFactory.getLogger(Client.class);
    private static Client instance = null;

    public Client()
    {
        try
        (
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            Authorization authorization = Authorization.getInstance();
        )
        {
            CommandLine commandLine = new CommandLine();

            String clientHello =
                    "The VFS client is run.\n"  +
                    "To connect for the server, please enter next command:\n" +
                    "Type 'connect server_name[:port] UserName' for connecting to the VFS server\n" +
                    "Type 'quit' command for disconnecting from the VFS server.\n" +
                    "Type 'exit' command for closing client.\n";
            System.out.println(clientHello);

            for (;;)
            {
                try
                {
                    String inputCommand = keyboard.readLine().trim();

                    User user = authorization.getUser();
                    Context context = commandLine.execute(user, inputCommand);

                    if(context.isThreadClose())
                    {
                        break;
                    }

                    if(context.getMessage() != null)
                    {
                        System.out.println(context.getMessage());
                    }
                }
                catch (IOException e)
                {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
        catch(Exception error)
        {
            System.err.println(error.getLocalizedMessage());
        }
    }

    public static Client run()
    {
        Client localInstance = instance;
        if(localInstance == null)
        {
            synchronized (Client.class)
            {
                localInstance = instance;
                if(localInstance == null)
                {
                    instance = localInstance = new Client();
                }
            }
        }
        return localInstance;
    }
}
