package org.vfs.client.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vfs.client.model.Authorization;
import org.vfs.client.model.CommandParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author Lipatov Nikita
 */
public class Client 
{

    private static final Logger log = LoggerFactory.getLogger(Client.class);
    private static Client instance = null;

    private Authorization authorization;

    public Client()
    {
        try
        {
            this.authorization = new Authorization();
            CommandParser parser = new CommandParser(this.authorization);
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

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
                    if(!parser.parserClientCommand(inputCommand))
                    {
                        break;
                    }
                    if(!parser.isEmptyMessage())
                    {
                        System.out.println(parser.getOutMessage());
                    }
                }
                catch (Exception e)
                {
                    if(keyboard != null)
                    {
                        keyboard.close();
                    }
                    System.err.println(e.getLocalizedMessage());
                    if(authorization.getConnection()!= null)
                    {
                        authorization.getConnection().kill();
                    }
                }
            }
        }
        catch(Throwable error)
        {
            System.err.println(error.getMessage());
            if(authorization.getConnection()!= null)
            {
                authorization.getConnection().kill();
            }
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
