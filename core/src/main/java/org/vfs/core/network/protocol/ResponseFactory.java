package org.vfs.core.network.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author Lipatov Nikita
 */
public class ResponseFactory {
    private static final Logger log = LoggerFactory.getLogger(ResponseFactory.class);

    public static Protocol.Response newResponse(Protocol.Response.ResponseType status, String message) {
        return Protocol.Response.newBuilder()
                .setCode(status)
                .setMessage(message)
                .build();
    }

    public static Protocol.Response newResponse(Protocol.Response.ResponseType status, String message, String specificCode) {
        return Protocol.Response.newBuilder()
                .setCode(status)
                .setMessage(message)
                .setSpecificCode(specificCode)
                .build();
    }

    public static Protocol.Response newResponse(SocketChannel channel) {
        ByteBuffer buffer = ByteBuffer.allocate(2 * 1024);
        int numRead = -1;

        try {
            numRead = channel.read(buffer); // get message from client

            if(numRead == -1) {
                log.debug("Connection closed by: {}", channel.getRemoteAddress());
                channel.close();
            }

            byte[] data = new byte[numRead];
            System.arraycopy(buffer.array(), 0, data, 0, numRead);

            Protocol.Response response = Protocol.Response.parseFrom(data);
            return response;
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
