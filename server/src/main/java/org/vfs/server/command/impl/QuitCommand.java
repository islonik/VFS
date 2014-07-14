package org.vfs.server.command.impl;

import org.vfs.core.network.protocol.Response;
import org.vfs.core.network.protocol.User;
import org.vfs.core.command.Command;
import org.vfs.core.model.Context;
import org.vfs.server.user.UserRegistry;

/**
 * @author Lipatov Nikita
 */
public class QuitCommand extends AbstractServerCommand implements Command
{
    public static final String USER_NOT_REMOVED = "User wasn't removed! Please try again!";
    public static final String SERVER_DISCONNECT = "You are disconnected from server!";

    public QuitCommand()
    {
        this.commandName = "quit";
    }

    public void action(Context context)
    {
        User user = context.getUser();

        if(UserRegistry.getInstance().removeUser(user.getId(), user.getLogin()))
        {
            //context.setUser(null);
            context.setCode(Response.STATUS_SUCCESS_QUIT); // quit
            context.setCommandWasExecuted(true);
            context.setBroadcastCommand(true);
            context.setExit(true);
            context.setMessage(SERVER_DISCONNECT);
        }
        else
        {
            context.setCode(Response.STATUS_FAIL_QUIT);
            context.setErrorMessage(USER_NOT_REMOVED);
        }
    }
}
