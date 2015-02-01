package org.vfs.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vfs.client.network.*;
import org.vfs.core.exceptions.QuitException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Lipatov Nikita
 */
public class Client {
    private static final Logger log = LoggerFactory.getLogger(Client.class);
    private final NetworkManager networkManager;
    private final UserManager userManager;

    public Client() throws IOException {
        userManager = new UserManager();
        networkManager = new NetworkManager();

        final ExecutorService executorService = Executors.newFixedThreadPool(1);

        final MessageSender messageSender = new MessageSender();
        networkManager.setMessageSender(messageSender);

        final IncomingMessageHandler incomingMessageHandler = new IncomingMessageHandler(userManager);

        final IncomingMessageListener incomingMessageListener = new IncomingMessageListener(networkManager, userManager, incomingMessageHandler);

        // loop
        executorService.execute(incomingMessageListener);
    }

    public void run() {
        try
        (
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        ) {
            CommandLine commandLine = new CommandLine(userManager, networkManager);

            String clientHello =
                    "The VFS client is run.\n" +
                    "To connect for the server, please enter next command:\n" +
                    "Type 'connect server_name:port UserName' for connecting to the VFS server\n" +
                    "Type 'quit' command for disconnecting from the VFS server.\n" +
                    "Type 'exit' command for closing client.\n";
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
            System.err.println(error.getLocalizedMessage());
        } finally {
            System.exit(1);
        }
    }
}
