package org.vfs.server;

import java.net.*;
import java.io.*;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.core.exceptions.QuitException;
import org.vfs.server.commands.Command;
import org.vfs.server.model.Timer;
import org.vfs.server.model.UserSession;
import org.vfs.server.network.*;
import org.vfs.server.services.NodeService;
import org.vfs.server.services.UserService;

import static org.vfs.core.network.protocol.Response.STATUS_SUCCESS_QUIT;
import static org.vfs.core.network.protocol.ResponseFactory.newResponse;

/**
 * Server class.
 *
 * @author Lipatov Nikita
 */
@Component
public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);

    private final ExecutorService executorService;
    private final NetworkManager networkManager;
    private final NodeService nodeService;
    private final UserService userService;
    private final Map<String, Command> commands;

    @Autowired
    public Server(ExecutorService executorService, NetworkManager networkManager, NodeService nodeService, UserService userService, Map<String, Command> commands) throws IOException {
        this.executorService = executorService;
        this.networkManager = networkManager;
        this.nodeService = nodeService;
        this.userService = userService;
        this.commands = commands;

        String out = "Server has been run!";
        this.nodeService.initDirs();

        System.out.println(out);
        log.info(out);
    }

    public void run() throws IOException {
        while (true) {
            final Socket socket = networkManager.accept();

            final Timer timer = new Timer();

            final MessageReader messageReader = new MessageReader(socket.getInputStream());
            final ClientWriter clientWriter = new ClientWriter(socket);

            final UserSession userSession = userService.startSession(socket, timer, clientWriter);

            System.out.println("New socket has been accepted! User id is " + userSession.getUser().getId());

            final CommandLine commandLine = new CommandLine(commands, userSession);
            final ClientListener clientListener = new ClientListener(messageReader, userService, userSession, commandLine, timer);

            Runnable connection = new Runnable() {
                @Override
                public void run() {
                    UserSession localUserSession = userSession;
                    try {
                        clientListener.listen();
                    } catch (QuitException qe) {
                        System.out.println(qe.getMessage());

                        // 'quit response' should be already sent in QuitCommand
                        userService.stopSession(localUserSession.getUser().getId());
                    }
                }
            };
            Future ftask = executorService.submit(connection);

            userSession.setTask(ftask);
        }
    }
}

