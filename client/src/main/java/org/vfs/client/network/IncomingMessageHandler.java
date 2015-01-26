package org.vfs.client.network;

import org.vfs.core.exceptions.QuitException;
import org.vfs.core.network.protocol.Protocol;

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

    public void handle(Protocol.Response response) {
        Protocol.Response.ResponseType code = response.getCode();

        String message = response.getMessage();

        Protocol.User user = userManager.getUser();

        switch (code) {
            case SUCCESS_CONNECT:  // success authorization
                user = Protocol.User.newBuilder()
                        .setId(response.getSpecificCode())
                        .setLogin(user.getLogin())
                        .build();
                userManager.setUser(user);
                networkManager.getMessageSender().setConnected(true);
                System.out.println(message);
                break;
            case FAIL_CONNECT:     // fail authorization
                networkManager.getMessageSender().setConnected(false);
                System.err.println(message);
                throw new QuitException("Such user already exist!");
            case SUCCESS_QUIT:     // quit response
                networkManager.getMessageSender().setConnected(false);
                System.out.println(message);
                throw new QuitException("Closing connection by client request");
            case FAIL:
                System.err.println(message);
                break;
            default:
                System.out.println(message);
        }
    }
}
