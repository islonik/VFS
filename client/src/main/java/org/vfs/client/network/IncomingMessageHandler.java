package org.vfs.client.network;

import org.vfs.core.network.protocol.Response;
import org.vfs.core.network.protocol.User;
import org.vfs.core.network.protocol.XmlHelper;

/**
 * @author Lipatov Nikita
 */
public class IncomingMessageHandler {

    private final UserManager userManager;
    private final NetworkManager networkManager;
    private XmlHelper xmlHelper;

    public IncomingMessageHandler(UserManager userManager, NetworkManager networkManager) {
        this.userManager = userManager;
        this.networkManager = networkManager;
        this.xmlHelper = new XmlHelper();
    }

    public void handle(String messageFromServer) {

        Response response = xmlHelper.unmarshal(Response.class, messageFromServer);

        int code = response.getCode();
        String message = response.getMessage();

        User user = userManager.getUser();

        switch (code) {
            case Response.STATUS_SUCCESS_CONNECT:  // success authorization
                user.setId(response.getSpecificCode());
                userManager.setUser(user);
                System.out.println(message);
                break;
            case Response.STATUS_FAIL_CONNECT:     // fail authorization
                userManager.setUser(null);
                networkManager.closeSocket();
                System.err.println(message);
                break;
            case Response.STATUS_SUCCESS_QUIT:     // quit response
                userManager.setUser(null);
                networkManager.closeSocket();
                System.out.println(message);
                break;
            case Response.STATUS_FAIL:
                System.err.println(message);
                break;
            default:
                System.out.println(message);
        }
    }
}
