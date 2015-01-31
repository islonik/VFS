package org.vfs.server;

import java.net.*;
import java.io.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.core.VFSConstants;
import org.vfs.core.exceptions.QuitException;
import org.vfs.core.network.protocol.Protocol;
import org.vfs.core.network.protocol.RequestFactory;
import org.vfs.server.commands.Command;
import org.vfs.server.model.UserSession;
import org.vfs.server.network.*;
import org.vfs.server.services.UserSessionService;

import static java.nio.channels.SelectionKey.OP_ACCEPT;
import static java.nio.channels.SelectionKey.OP_READ;

/**
 * Server class.
 *
 * @author Lipatov Nikita
 */
@Component
public class Server implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(Server.class);

    private final NetworkConfig networkConfig;
    private final UserSessionService userSessionService;
    private final Map<String, Command> commands;
    private final CommandLine commandLine;

    @Autowired
    public Server(NetworkConfig networkConfig, UserSessionService userSessionService, Map<String, Command> commands) throws IOException {
        this.networkConfig = networkConfig;
        this.userSessionService = userSessionService;
        this.commands = commands;

        this.commandLine = new CommandLine(commands);

        String out = "Server has been run!";

        System.out.println(out);
        log.info(out);
    }

    public void run() {
        Selector selector = null;
        ServerSocketChannel server = null;
        String address = networkConfig.getAddress();
        int port = networkConfig.getPort();

        try {
            log.debug("Starting server...");

            selector = Selector.open();
            server = ServerSocketChannel.open();
            server.socket().bind(new InetSocketAddress(address, port));
            server.configureBlocking(false);
            server.register(selector, OP_ACCEPT);

            log.debug("Server ready, now ready to accept connections");
            loop(selector, server);

        } catch (Throwable e) {
            log.error("Server failure", e);
        } finally {
            try {
                selector.close();
                server.socket().close();
                server.close();
            } catch (Exception e) {
                // do nothing - server failed
            }
        }
    }

    private void loop(Selector selector, ServerSocketChannel server) throws IOException, InterruptedException {
        while(true){
            int num = selector.select();
            if(num == 0) {
                continue;
            }
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

            while(keys.hasNext()){
                SelectionKey key = keys.next();
                keys.remove();

                if(key.isConnectable()){
                    log.debug("Connectable detected");
                    ((SocketChannel) key.channel()).finishConnect();
                } else if(key.isAcceptable()){
                    acceptOp(key, selector, server);
                } else if(key.isReadable()){
                    readOp(key);
                }
            }
        }
    }

    private void acceptOp(SelectionKey key, Selector selector, ServerSocketChannel server) throws IOException {
        SocketChannel client = server.accept();

        log.debug("Acceptable detected, incoming client: {}", client.getRemoteAddress());
        System.out.println("New socket has been accepted! User id is " + client.getRemoteAddress());

        client.configureBlocking(false);
        client.register(selector, OP_READ);
    }

    private void readOp(SelectionKey key) throws IOException, InterruptedException {
        try {
            log.debug("Data received, going to read them");
            SocketChannel channel = (SocketChannel) key.channel();

            Protocol.Request request = RequestFactory.newRequest(channel);

            if(request == null) {
                log.error("Protocol.Request is null! Channel is going to be closed!");
                key.channel().close();
                key.cancel();
                return;
            }

            System.out.println("DEBUG: " + request.toString());

            UserSession userSession;
            if(request.getUser().getId().equals(VFSConstants.NEW_USER)) {
                ClientWriter clientWriter = new ClientWriter(key);

                userSession = userSessionService.startSession(clientWriter);
            } else {
                userSession = userSessionService.getSession(request.getUser().getId());
            }

            userSession.getTimer().updateTime();

            commandLine.onUserInput(userSession, request); // QuitException can be thrown here
        } catch(QuitException qe) { // quit logic
            System.out.println("QuitException was detected! Address " + ((SocketChannel) key.channel()).getRemoteAddress().toString() + " was closed!");
            key.channel().close();
            key.cancel();
        }
    }



}

