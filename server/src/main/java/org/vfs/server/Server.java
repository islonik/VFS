package org.vfs.server;

import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vfs.server.exceptions.QuitException;
import org.vfs.server.model.UserSession;
import org.vfs.server.network.*;
import org.vfs.server.services.LockService;
import org.vfs.server.services.NodeService;
import org.vfs.server.services.UserService;

/**
 * Server class.
 *
 * @author Lipatov Nikita
 */
public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);

    private final ExecutorService executorService;
    private final NetworkManager networkManager;
    private final LockService lockService;
    private final NodeService nodeService;
    private final UserService userService;

    public Server() throws IOException {
        executorService = Executors.newCachedThreadPool();
        networkManager = new NetworkManager("localhost", 4499, 100);
        lockService = new LockService();
        nodeService = new NodeService("/", lockService);
        userService = new UserService(nodeService);

        String out = "Server has been run!";
        System.out.println(out);
        log.info(out);
    }

    public void run() throws IOException {

        while(true)
        {
            Socket socket = networkManager.accept();

            UserSession userSession = userService.startSession();
            userSession.setSocket(socket);

            System.out.println("New socket has been accepted! User id is " + userSession.getUser().getId());

            MessageReader messageReader = new MessageReader(socket.getInputStream());
            final ClientWriter clientWriter = new ClientWriter(userSession);
            final CommandLine commandLine = new CommandLine(lockService, nodeService, userService, userSession, clientWriter);
            final ClientListener clientListener = new ClientListener(messageReader, userService, commandLine);

            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        clientListener.listen();
                    } catch(QuitException qe) {
                        System.out.println(qe.getMessage());
                    }
                }
            });

        }

    }
}
