package org.vfs.client.network;

import org.vfs.core.network.protocol.Protocol;
import org.vfs.core.network.protocol.RequestFactory;

import java.util.concurrent.BlockingQueue;

/**
 * BlockingQueue should use non-blocking API.
 *
 * @author Lipatov Nikita
 */
public class MessageSender {
    private BlockingQueue<Protocol.Request> toServerQueue;

    public MessageSender(BlockingQueue<Protocol.Request> queue) {
        this.toServerQueue = queue;
    }

    public boolean send(Protocol.User user, String command) {
        if (user != null) {
            Protocol.Request request = RequestFactory.newRequest(user.getId(), user.getLogin(), command);

            return this.toServerQueue.add(request);
        }
        return false;
    }
}
