package org.vfs.core.network.protocol.proto;

/**
 * @author Lipatov Nikita
 */
public class RequestFactory {

    public static RequestProto.Request newRequest(String userId, String userLogin, String command) {
        RequestProto.Request.User user = RequestProto.Request.User.newBuilder()
                .setId(userId)
                .setLogin(userLogin)
                .build();
        return RequestProto.Request.newBuilder()
                .setUser(user)
                .setCommand(command)
                .build();
    }
}
