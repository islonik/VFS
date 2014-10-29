package org.vfs.client.network;

import org.vfs.core.network.protocol.proto.RequestProto;
import org.vfs.core.network.protocol.proto.ResponseProto;

/**
 * @author Lipatov Nikita
 */
public class IncomingMessageHandler {

    private final UserManager userManager;
    private final NetworkManager networkManager;

    public IncomingMessageHandler(UserManager userManager, NetworkManager networkManager) {
        this.userManager = userManager;
        this.networkManager = networkManager;
    }

    public void handle(ResponseProto.Response response) {

        ResponseProto.Response.ResponseType code = response.getCode();

        String message = response.getMessage();

        RequestProto.Request.User user = userManager.getUser();

        switch (code) {
            case SUCCESS_CONNECT:  // success authorization
                user = RequestProto.Request.User.newBuilder()
                        .setId(response.getSpecificCode())
                        .setLogin(user.getLogin())
                        .build();
                userManager.setUser(user);
                System.out.println(message);
                break;
            case FAIL_CONNECT:     // fail authorization
                userManager.setUser(null);
                networkManager.closeSocket();
                System.err.println(message);
                break;
            case SUCCESS_QUIT:     // quit response
                userManager.setUser(null);
                networkManager.closeSocket();
                System.out.println(message);
                break;
            case FAIL:
                System.err.println(message);
                break;
            default:
                System.out.println(message);
        }
    }
}
