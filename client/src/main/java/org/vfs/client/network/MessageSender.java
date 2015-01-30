package org.vfs.client.network;

import org.vfs.core.network.protocol.Protocol;
import org.vfs.core.network.protocol.RequestFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;

/**
 * BlockingQueue should use non-blocking API.
 *
 * @author Lipatov Nikita
 */
public class MessageSender {

    private volatile boolean isConnected = false;
    private volatile SelectionKey key;
    private BlockingQueue<Protocol.Request> toServerQueue;

    public MessageSender(BlockingQueue<Protocol.Request> queue) {
        this.toServerQueue = queue;
    }

    public void setKey(SelectionKey key) {
        this.key = key;
        synchronized (this) {
            notifyAll();
        }
    }

    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public boolean send(Protocol.User user, String command) {
        if (user != null) {
            Protocol.Request request = RequestFactory.newRequest(user.getId(), user.getLogin(), command);
            // TODO: fix this?
            try {
                if (!isConnected) {
                    this.toServerQueue.add(request);
                } else {
                    SocketChannel channel = (SocketChannel) key.channel();
                    key.interestOps(SelectionKey.OP_WRITE);

                    ByteBuffer writeBuffer = ByteBuffer.wrap(request.toByteString().toByteArray());
                    channel.write(writeBuffer);

                    key.interestOps(SelectionKey.OP_READ);
                }
            } catch (IOException ie) {
                System.out.println("MessageSender = " + ie);
            }
            return true;
        }
        return false;
    }
}
