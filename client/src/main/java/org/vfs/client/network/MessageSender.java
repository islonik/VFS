package org.vfs.client.network;

import io.netty.channel.Channel;
import org.vfs.core.network.protocol.Protocol.Request;
import org.vfs.core.network.protocol.Protocol.User;
import org.vfs.core.network.protocol.RequestFactory;

/**
 * BlockingQueue should use non-blocking API.
 *
 * @author Lipatov Nikita
 */
public class MessageSender {

    private volatile Channel channel;

    public void setChannel(Channel channel) {
        synchronized (this) {
            this.channel = channel;
            notifyAll();
        }
    }

    /**
     * API method. Please don't change incoming parameters or name of method!
     */
    public boolean send(User user, String command) {
        if (user != null) {
            Request request = RequestFactory.newRequest(user.getId(), user.getLogin(), command);
            try {
                if (channel == null) {
                    synchronized (this) {
                        while(channel == null) {
                            wait();
                        }
                    }
                }

                channel.writeAndFlush(request);
            } catch (InterruptedException ie) {
                System.out.println("MessageSender = " + ie);
            }
            return true;
        }
        return false;
    }
}
