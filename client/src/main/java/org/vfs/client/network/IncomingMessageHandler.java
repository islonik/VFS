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
        if (Response.STATUS_SUCCESS_CONNECT == code) {  // success authorization
            user.setId(response.getSpecificCode());
            userManager.setUser(user);
        } else if (Response.STATUS_FAIL_CONNECT == code) { // fail authorization
            userManager.setUser(null);
            networkManager.closeSocket();
        } else if (Response.STATUS_SUCCESS_QUIT == code) { // quit response
            userManager.setUser(null);
            networkManager.closeSocket();
        }

        System.out.println(message);
    }
}
