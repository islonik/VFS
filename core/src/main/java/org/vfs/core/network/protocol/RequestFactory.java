package org.vfs.core.network.protocol;

/**
 * @author Lipatov Nikita
 */
public class RequestFactory {

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
}
