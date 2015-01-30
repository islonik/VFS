package org.vfs.server;

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.core.exceptions.QuitException;
import org.vfs.core.network.protocol.Protocol;
import org.vfs.server.commands.Command;
import org.vfs.server.model.UserSession;
import org.vfs.server.network.*;
import org.vfs.server.services.NodeService;
import org.vfs.server.services.UserService;

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

    private final ExecutorService executorService;
    private final NetworkManager networkManager;
    private final NodeService nodeService;
    private final UserService userService;
    private final Map<String, Command> commands;
    private final CommandLine commandLine;
    private final BlockingQueue<Protocol.Response> toUsersQueue;

    @Autowired
    public Server(ExecutorService executorService, NetworkManager networkManager, NodeService nodeService, UserService userService, Map<String, Command> commands) throws IOException {
        this.executorService = executorService;
        this.networkManager = networkManager;
        this.nodeService = nodeService;
        this.userService = userService;
        this.commands = commands;

        this.toUsersQueue = new ArrayBlockingQueue<Protocol.Response>(1024);
        this.commandLine = new CommandLine(commands);

        String out = "Server has been run!";
        this.nodeService.initDirs();

        System.out.println(out);
        log.info(out);
    }

    public void run() {
        Selector selector = null;
        ServerSocketChannel server = null;
        String address = networkManager.getAddress();
        int port = networkManager.getPort();

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

            Protocol.Request request = readRequest(channel);

            if(request == null) {
                log.error("Protocol.Request is null! Channel is going to be closed!");
                key.channel().close();
                key.cancel();
                return;
            }

            System.out.println(request.toString());

            UserSession userSession = null;
            if(request.getUser().getId().equals("0")) {
                ClientWriter clientWriter = new ClientWriter(key, toUsersQueue);

                userSession = userService.startSession(clientWriter);
            } else {
                userSession = userService.getSession(request.getUser().getId());
            }

            userSession.getTimer().updateTime();

            commandLine.onUserInput(userSession, request); // QuitException could is thrown from here
        } catch(QuitException qe) { // quit logic
            System.out.println("QuitException was detected! Address " + ((SocketChannel) key.channel()).getRemoteAddress().toString() + " was closed!");
            key.channel().close();
            key.cancel();
        }
    }

    private Protocol.Request readRequest(SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(2 * 1024);
        int numRead = -1;

        try {
            numRead = channel.read(buffer); // get message from client

            if(numRead == -1) {
                log.debug("Connection closed by: {}", channel.getRemoteAddress());
                channel.close();
                return null;
            }

            byte[] data = new byte[numRead];
            System.arraycopy(buffer.array(), 0, data, 0, numRead);
            Protocol.Request request = Protocol.Request.parseFrom(data);
            return request;
        } catch (Exception e) {
            log.error("Unable to read from channel", e);
            try {
                channel.close();
            } catch (IOException e1) {
                //nothing to do, channel dead
            }
        }
        return null;
    }

}

