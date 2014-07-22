package org.vfs.client.network;

import org.vfs.core.network.protocol.Request;
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
    private final XmlHelper xmlHelper;

    public MessageSender(BlockingQueue<String> queue) {
        this.toServerQueue = queue;

        xmlHelper = new XmlHelper();
    }

    public boolean send(User user, String command) {
        if (user != null) {
            RequestFactory requestFactory = new RequestFactory();
            Request request = requestFactory.create(user.getId(), user.getLogin(), command);
            String requestXml = xmlHelper.marshal(Request.class, request);

            return this.toServerQueue.add(requestXml);
        }
        return false;
    }
}
