package org.vfs.core.network.protocol.proto;

/**
 * @author Lipatov Nikita
 */
public class ResponseFactory {

    public static ResponseProto.Response newResponse(ResponseProto.Response.ResponseType status, String message) {

        return ResponseProto.Response.newBuilder()
                .setCode(status)
                .setMessage(message)
                .build();
    }

    public static ResponseProto.Response newResponse(ResponseProto.Response.ResponseType status, String message, String specificCode) {

        return ResponseProto.Response.newBuilder()
                .setCode(status)
                .setMessage(message)
                .setSpecificCode(specificCode)
                .build();
    }
}
