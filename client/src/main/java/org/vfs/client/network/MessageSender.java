package org.vfs.client.network;

import org.vfs.core.network.protocol.RequestFactory;
import org.vfs.core.network.protocol.User;
import org.vfs.core.network.protocol.XmlHelper;

import java.util.concurrent.BlockingQueue;

/**
 * BlockingQueue should use non-blocking API.
 *
 * @author Lipatov Nikita
 */
public class MessageSender {
    private BlockingQueue<String> toServerQueue;

    public MessageSender(BlockingQueue<String> queue) {
        this.toServerQueue = queue;
    }

    public boolean send(User user, String command) {
        if (user != null) {
            String requestXml = RequestFactory.newRequest(user.getId(), user.getLogin(), command);

            return this.toServerQueue.add(requestXml);
        }
        return false;
    }
}
