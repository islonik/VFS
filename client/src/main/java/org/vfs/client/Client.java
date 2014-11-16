package org.vfs.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vfs.client.network.IncomingMessageHandler;
import org.vfs.client.network.MessageSender;
import org.vfs.client.network.UserManager;
import org.vfs.client.network.IncomingMessageListener;
import org.vfs.client.network.NetworkManager;
import org.vfs.client.network.SocketReader;
import org.vfs.client.network.SocketWriter;
import org.vfs.core.exceptions.QuitException;
import org.vfs.core.network.protocol.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
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
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        BlockingQueue<Protocol.Response> toUserQueue = new ArrayBlockingQueue<Protocol.Response>(1024);
        BlockingQueue<Protocol.Request> toServerQueue = new ArrayBlockingQueue<Protocol.Request>(1024);

        final MessageSender messageSender = new MessageSender(toServerQueue);
        networkManager = new NetworkManager();
        networkManager.setMessageSender(messageSender);
        IncomingMessageHandler handler = new IncomingMessageHandler(userManager, networkManager);

        final SocketReader socketReader = new SocketReader(toUserQueue, networkManager);
        final SocketWriter socketWriter = new SocketWriter(toServerQueue, networkManager);

        final IncomingMessageListener incomingMessageListener = new IncomingMessageListener(toUserQueue, handler);

        executorService.execute
        (
            new Runnable() {
                @Override
                public void run() {
                    socketReader.run();
                }
            }
        );

        executorService.execute
        (
            new Runnable() {
                @Override
                public void run() {
                    socketWriter.run();
                }
            }
        );

        executorService.execute
        (
            new Runnable() {
                @Override
                public void run() {
                    incomingMessageListener.run();
                }
            }
        );
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
