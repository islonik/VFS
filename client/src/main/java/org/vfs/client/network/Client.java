package org.vfs.client.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vfs.client.command.CommandLine;
import org.vfs.client.model.UserManager;
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

    public void run()
    {
        try
        (
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            ClientConnectionManager manager = ClientConnectionManager.getInstance();
        )
        {
            UserManager userManager = UserManager.getInstance();
            CommandLine commandLine = new CommandLine();

            String clientHello =
                "The VFS client is run.\n"  +
                "To connect for the server, please enter next command:\n" +
                "Type 'connect server_name:port UserName' for connecting to the VFS server\n" +
                "Type 'quit' command for disconnecting from the VFS server.\n" +
                "Type 'exit' command for closing client.\n";
            System.out.println(clientHello);

            while(true)
            {
                try
                {
                    String inputCommand = keyboard.readLine().trim();

                    User user = userManager.getUser();
                    Context context = commandLine.execute(user, inputCommand);

                    if(context.getMessage() != null)
                    {
                        System.out.println(context.getMessage());
                    }

                    if(context.isExit())
                    {
                        break;
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
}
