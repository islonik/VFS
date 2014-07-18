package org.vfs.client.model;

import org.vfs.core.network.protocol.Response;
import org.vfs.core.network.protocol.ResponseService;
import org.vfs.core.network.protocol.User;

/**
 * @author Lipatov Nikita
 */
public class IncomingMessageHandler
{

    public void handle(String messageFromServer)
    {
        ResponseService responseService = new ResponseService();
        Response response = responseService.parse(messageFromServer);
        int code       = Integer.parseInt(response.getCode());
        String message = response.getMessage();

        UserManager userManager = UserManager.getInstance();
        User user = userManager.getUser();
        if(Response.STATUS_SUCCESS_CONNECT == code)   // success authorization
        {
            user.setId(response.getSpecificCode());
            userManager.setUser(user);
        }
        else if(Response.STATUS_FAIL_CONNECT == code) // fail authorization
        {
            UserManager.getInstance().setUser(null);
        }
        else if(Response.STATUS_SUCCESS_QUIT == code)  // quit response
        {
            UserManager.getInstance().setUser(null);
        }

        System.out.println(message);
    }
}
