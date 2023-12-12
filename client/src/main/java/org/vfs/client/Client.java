package org.vfs.client;

import org.vfs.client.network.*;
import org.vfs.core.exceptions.QuitException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Lipatov Nikita
 */
public class Client {
    final UserManager userManager;
    final MessageSender messageSender;
    final NetworkManager networkManager;

    public Client() {
        this.userManager = new UserManager();
        this.messageSender = new MessageSender();
        this.networkManager = new NetworkManager(userManager, messageSender);
    }

    public void run() {
        try
        (
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in))
        ) {
            CommandLine commandLine = new CommandLine(userManager, networkManager);

            String clientHello =
                    """
                    VFS client is running.
                    To connect for the VFS server, please enter next command:
                    Type 'connect server_name:port UserName' for connecting to the VFS server
                    Type 'quit' command for disconnecting from the VFS server.
                    Type 'exit' command for closing client.
                    """;
            System.out.println(clientHello);

            while (true) {
                try {
                    String inputCommand = keyboard.readLine().trim();

                    commandLine.execute(inputCommand);
                } catch (IOException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        } catch (QuitException qe) {
            System.out.println(qe.getMessage());
        } catch (Exception error) {
            if (!error.getLocalizedMessage().isEmpty()) {
                System.err.println(error.getLocalizedMessage());
            }
        } finally {
            System.exit(1);
        }
    }
}
