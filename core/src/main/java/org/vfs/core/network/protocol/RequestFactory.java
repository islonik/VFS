package org.vfs.core.network.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}
