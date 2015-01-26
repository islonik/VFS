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

    private boolean isConnected = false;
    private SelectionKey key;
    private BlockingQueue<Protocol.Request> toServerQueue;

    public MessageSender(BlockingQueue<Protocol.Request> queue) {
        this.toServerQueue = queue;
    }

    public void setKey(SelectionKey key) {
        this.key = key;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public boolean send(Protocol.User user, String command) {
        if (user != null) {
            Protocol.Request request = RequestFactory.newRequest(user.getId(), user.getLogin(), command);
            if (!isConnected) {
                this.toServerQueue.add(request);
                return true;
            } else {
                try {
                    SocketChannel channel = (SocketChannel) key.channel();
                    key.interestOps(SelectionKey.OP_WRITE);

                    ByteBuffer writeBuffer = ByteBuffer.wrap(request.toByteString().toByteArray());
                    channel.write(writeBuffer);

                    key.interestOps(SelectionKey.OP_READ);

                    return true;
                } catch (IOException ioe) {
                    System.out.println("MessageSender = " + ioe);
                }
                return true;
            }
        }
        return false;
    }
}
