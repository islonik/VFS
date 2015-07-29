package org.vfs.core.network.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}
