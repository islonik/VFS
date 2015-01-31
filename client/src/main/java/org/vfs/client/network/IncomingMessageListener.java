package org.vfs.client.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vfs.core.exceptions.QuitException;
import org.vfs.core.network.protocol.Protocol;
import org.vfs.core.network.protocol.RequestFactory;
import org.vfs.core.network.protocol.ResponseFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

/**
 * @author Lipatov Nikita
 */
public class IncomingMessageListener implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(IncomingMessageHandler.class);

    private final NetworkManager networkManager;
    private final UserManager userManager;
    private final IncomingMessageHandler incomingMessageHandler;

    public IncomingMessageListener(
            NetworkManager networkManager,
            UserManager userManager,
            IncomingMessageHandler incomingMessageHandler) {
        this.networkManager = networkManager;
        this.userManager = userManager;
        this.incomingMessageHandler = incomingMessageHandler;
    }

    @Override
    public void run() {
        while(true) {
            Selector selector = networkManager.getSelector();
            MessageSender messageSender = networkManager.getMessageSender();

            try {
                while(true) {
                    selector.select();
                    Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                    while(keys.hasNext()) {
                        SelectionKey key = keys.next();
                        keys.remove();

                        // Get the socket channel held by the key
                        SocketChannel channel = (SocketChannel)key.channel();

                        // Attempt a connection
                        if (key.isConnectable()) { // connect command
                            // Close pendent connections
                            if (channel.isConnectionPending()) {
                                channel.finishConnect();
                            }
                            messageSender.setKey(key); // message will send after it
                        } else if(key.isReadable()) {
                            Protocol.Response response = ResponseFactory.newResponse(channel);
                            incomingMessageHandler.handle(response);
                        }
                    }
                }
            } catch (IOException | QuitException err) {
                userManager.setUser(null);
                networkManager.closeSocket();
                messageSender.setKey(null);

                if(!(err instanceof QuitException)) {
                    System.err.println(err.getMessage());
                }
            }
        }
    }
}
