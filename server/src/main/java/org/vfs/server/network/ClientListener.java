package org.vfs.server.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vfs.server.CommandLine;
import org.vfs.server.model.Timer;
import org.vfs.server.model.UserSession;
import org.vfs.server.services.UserService;

import java.io.IOException;
import java.net.Socket;

/**
 * @author Lipatov Nikita
 */
public class ClientListener {
    private static final Logger log = LoggerFactory.getLogger(ClientListener.class);

    private final MessageReader reader;
    private final UserService userService;
    private final UserSession userSession;
    private final CommandLine commandLine;
    private final Timer timer;

    public ClientListener(MessageReader reader, UserService userService, UserSession userSession, CommandLine commandLine, Timer timer) {
        this.reader = reader;
        this.userService = userService;
        this.userSession = userSession;
        this.commandLine = commandLine;
        this.timer = timer;
    }

    public void listen() {
        while (true) {
            try {
                String message = reader.read();

                timer.updateTime();

                commandLine.onUserInput(message);
            } catch (IOException e) {
                log.error("Unable read client message!", e);
                Socket socket = userSession.getSocket();
                if (!socket.isClosed()) {
                    try {
                        socket.close();
                    } catch (IOException ie) {
                        log.error(ie.getMessage(), ie);
                    }
                }
                userService.stopSession(userSession.getUser().getId());
                break;
            }
        }
    }
}
