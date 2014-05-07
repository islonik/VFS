package org.vfs.server.user;

import org.vfs.core.network.protocol.Request;
import org.vfs.server.command.impl.ConnectCommand;

/**
 * @author Lipatov Nikita
 */
public class UserSecurity
{

    public boolean isSecure(Request request)
    {

        String id      = request.getUserId();
        String login   = request.getUserLogin();
        String command = request.getCommand().toLowerCase().trim();

        User user = UserRegistry.getInstance().getUser(login);

        // user should have id and should be authorized
        if(user != null)
        {
            if(Long.toString(user.getId()).equals(id))
            {
                return true;
            }
        }
        // user may be without id, but in that case the command should be connect
        if(command != null && command.startsWith(ConnectCommand.CONNECT))
        {
            return true;
        }
        // something wrong
        return false;
    }

}
