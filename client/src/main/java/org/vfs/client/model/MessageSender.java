package org.vfs.client.model;

import org.vfs.core.network.protocol.Request;
import org.vfs.core.network.protocol.RequestService;
import org.vfs.core.network.protocol.User;

import java.util.concurrent.BlockingQueue;

/**
 * BlockingQueue should use non-blocking API.
 * @author Lipatov Nikita
 */
public class MessageSender
{
    private BlockingQueue<String> toServerQueue;

    public MessageSender(BlockingQueue<String> queue)
    {
        this.toServerQueue = queue;
    }

    public boolean add(String command)
    {
        UserManager userManager = UserManager.getInstance();
        User user = userManager.getUser();
        if(user != null)
        {
            RequestService requestService = new RequestService();
            Request request = requestService.create(user.getId(), user.getLogin(), command);
            String requestXml = requestService.toXml(request);

            return this.toServerQueue.add(requestXml);
        }

        return false;
    }
}
