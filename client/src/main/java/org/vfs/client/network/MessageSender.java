package org.vfs.client.network;

import org.vfs.core.network.protocol.Protocol;
import org.vfs.core.network.protocol.RequestFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * BlockingQueue should use non-blocking API.
 *
 * @author Lipatov Nikita
 */
public class MessageSender {

    private volatile SelectionKey key;

    public void setKey(SelectionKey key) {
        synchronized (this) {
            this.key = key;
            notifyAll();
        }
    }

    public boolean send(Protocol.User user, String command) {
        if (user != null) {
            Protocol.Request request = RequestFactory.newRequest(user.getId(), user.getLogin(), command);
            try {
                if (key == null) {
                    synchronized (this) {
                        while(key == null) {
                            wait();
                        }
                    }
                }
                key.interestOps(SelectionKey.OP_WRITE);

                SocketChannel channel = (SocketChannel)key.channel();
                ByteBuffer writeBuffer = ByteBuffer.wrap(request.toByteArray());
                channel.write(writeBuffer);

                key.interestOps(SelectionKey.OP_READ);

            } catch (IOException | InterruptedException ie) {
                System.out.println("MessageSender = " + ie);
            }
            return true;
        }
        return false;
    }
}
