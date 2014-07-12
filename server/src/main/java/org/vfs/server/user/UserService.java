package org.vfs.server.user;

import org.vfs.core.network.protocol.Request;
import org.vfs.core.network.protocol.User;
import org.vfs.server.command.impl.ConnectCommand;
import org.vfs.server.model.NodeFactory;

/**
 * @author Lipatov Nikita
 */
public class UserService
{

    public User create(String login)
    {
        User user = new User();
        user.setId(GeneratorID.getInstance().getId());
        user.setLogin(login);
        user.setDirectory(NodeFactory.getFactory().createDirectory("home/" + login));
        return user;
    }

    public boolean isSecure(Request request)
    {
        String id      = request.getUser().getId();
        String login   = request.getUser().getLogin();
        String command = request.getCommand().toLowerCase().trim();

        User user = UserRegistry.getInstance().getUser(login);

        // user should have id and should be authorized
        if(user != null)
        {
            if(user.getId().equals(id))
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
