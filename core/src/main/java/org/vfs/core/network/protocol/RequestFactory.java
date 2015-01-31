package org.vfs.core.network.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author Lipatov Nikita
 */
public class RequestFactory {
    private static final Logger log = LoggerFactory.getLogger(RequestFactory.class);

    public static Protocol.Request newRequest(String userId, String userLogin, String command) {
        Protocol.User user = Protocol.User.newBuilder()
                .setId(userId)
                .setLogin(userLogin)
                .build();
        return Protocol.Request.newBuilder()
                .setUser(user)
                .setCommand(command)
                .build();
    }

    public static Protocol.Request newRequest(SocketChannel channel) {
        ByteBuffer buffer = ByteBuffer.allocate(2 * 1024);
        int numRead = -1;

        try {
            numRead = channel.read(buffer); // get message from client

            if(numRead == -1) {
                log.debug("Connection closed by: {}", channel.getRemoteAddress());
                channel.close();
                return null;
            }

            byte[] data = new byte[numRead];
            System.arraycopy(buffer.array(), 0, data, 0, numRead);

            Protocol.Request request = Protocol.Request.parseFrom(data);
            return request;
        } catch (IOException e) {
            log.error("Unable to read from channel", e);
            try {
                channel.close();
            } catch (IOException e1) {
                //nothing to do, channel dead
            }
        }
        return null;
    }

}
