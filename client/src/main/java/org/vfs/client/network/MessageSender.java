package org.vfs.client.network;

import org.vfs.core.network.protocol.proto.RequestFactory;
import org.vfs.core.network.protocol.proto.RequestProto;

import java.util.concurrent.BlockingQueue;

/**
 * BlockingQueue should use non-blocking API.
 *
 * @author Lipatov Nikita
 */
public class MessageSender {
    private BlockingQueue<RequestProto.Request> toServerQueue;

    public MessageSender(BlockingQueue<RequestProto.Request> queue) {
        this.toServerQueue = queue;
    }

    public boolean send(RequestProto.Request.User user, String command) {
        if (user != null) {
            RequestProto.Request request = RequestFactory.newRequest(user.getId(), user.getLogin(), command);

            return this.toServerQueue.add(request);
        }
        return false;
    }
}
