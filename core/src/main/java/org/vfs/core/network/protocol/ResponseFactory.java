package org.vfs.core.network.protocol;

/**
 * @author Lipatov Nikita
 */
public class ResponseFactory {

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
