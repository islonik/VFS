package org.vfs.client.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vfs.core.exceptions.QuitException;
import org.vfs.core.network.protocol.Protocol;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;

/**
 * @author Lipatov Nikita
 */
public class IncomingMessageListener implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(IncomingMessageHandler.class);

    private final NetworkManager networkManager;
    private final UserManager userManager;
    private final IncomingMessageHandler incomingMessageHandler;
    private final BlockingQueue<Protocol.Request> toServerQueue;

    public IncomingMessageListener(
            NetworkManager networkManager,
            UserManager userManager,
            IncomingMessageHandler incomingMessageHandler,
            BlockingQueue<Protocol.Request> toServerQueue) {
        this.networkManager = networkManager;
        this.userManager = userManager;
        this.incomingMessageHandler = incomingMessageHandler;
        this.toServerQueue = toServerQueue;
    }

    @Override
    public void run() {
        while(true) {
            Selector selector = networkManager.getSelector();
            SocketChannel socketChannel = networkManager.getSocketChannel();
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
                            messageSender.setKey(key);
                            // TODO: fix it?
                            Protocol.Request request = toServerQueue.take();
                            socketChannel.register(selector, SelectionKey.OP_WRITE);
                            ByteBuffer writeBuffer = ByteBuffer.wrap(request.toByteString().toByteArray());
                            channel.write(writeBuffer);
                            socketChannel.register(selector, SelectionKey.OP_READ);

                        } else if(key.isReadable()) {
                            Protocol.Response response = readResponse(channel);
                            incomingMessageHandler.handle(response);
                        }
                    }
                }
            } catch (IOException | InterruptedException | QuitException err) {
                userManager.setUser(null);
                networkManager.closeSocket();

                if(!(err instanceof QuitException)) {
                    System.err.println(err.getMessage());
                }
            }
        }
    }

    private Protocol.Response readResponse(SocketChannel channel) {
        Protocol.Response response = null;
        ByteBuffer buffer = ByteBuffer.allocate(2 * 1024);
        int numRead = -1;

        try {
            numRead = channel.read(buffer); // get message from client

            if(numRead == -1) {
                log.debug("Connection closed by: {}", channel.getRemoteAddress());
                channel.close();
            }

            byte[] data = new byte[numRead];
            System.arraycopy(buffer.array(), 0, data, 0, numRead);

            response = Protocol.Response.parseFrom(data);
        } catch (Exception e) {
            log.error("Unable to read from channel", e);
            try {
                channel.close();
            } catch (IOException e1) {
                //nothing to do, channel dead
            }
        }
        if(response == null) {
            System.err.println("Response was not got from server!");
        }
        return response;
    }
}
