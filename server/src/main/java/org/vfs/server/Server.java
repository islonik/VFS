package org.vfs.server;

import java.net.*;
import java.io.*;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
//import org.vfs.server.aspects.NodeRegister;
import org.vfs.server.commands.Command;
import org.vfs.server.exceptions.QuitException;
import org.vfs.server.model.NodeTypes;
import org.vfs.server.model.UserSession;
import org.vfs.server.network.*;
import org.vfs.server.services.LockService;
import org.vfs.server.services.NodeService;
import org.vfs.server.services.UserService;

/**
 * Server class.
 *
 * @author Lipatov Nikita
 * TODO: 1) Test на LockService (несколько потоков - попытаться заблокировать одну и ту же ноду)
 * TODO: 2) @NodeRegisterAware создать аннотацию, которая будет инструментировать автоматически через spring-advice ноды, добавляя и удаляя их из LockService
 */
@Component
public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);

    private final ExecutorService executorService;
    private final NetworkManager networkManager;
    private final LockService lockService;
    private final NodeService nodeService;
    private final UserService userService;
    private final Map<String, Command> commands;

    @Autowired
    public Server(ExecutorService executorService, NetworkManager networkManager, LockService lockService, NodeService nodeService, UserService userService, Map<String, Command> commands) throws IOException {
        this.executorService = executorService;
        this.networkManager = networkManager;
        this.lockService = lockService;
        this.nodeService = nodeService;
        this.userService = userService;
        this.commands = commands;

        String out = "Server has been run!";
        System.out.println(out);
        log.info(out);
        nodeService.newNode("test", NodeTypes.DIR);
    }

    public void run() throws IOException {

        while (true) {
            Socket socket = networkManager.accept();

            UserSession userSession = userService.startSession();
            userSession.setSocket(socket);

            System.out.println("New socket has been accepted! User id is " + userSession.getUser().getId());

            MessageReader messageReader = new MessageReader(socket.getInputStream());
            final ClientWriter clientWriter = new ClientWriter(userSession);

            final CommandLine commandLine = new CommandLine(commands, userSession, clientWriter);
            final ClientListener clientListener = new ClientListener(messageReader, userService, commandLine);

            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        clientListener.listen();
                    } catch (QuitException qe) {
                        System.out.println(qe.getMessage());
                    }
                }
            });

        }

    }
}

