package org.vfs.server.command.impl;

import org.vfs.core.network.protocol.Response;
import org.vfs.server.command.Command;
import org.vfs.server.model.Context;
import org.vfs.server.user.User;
import org.vfs.server.user.UserRegistry;

import java.util.HashMap;

/**
 * @author Lipatov Nikita
 */
public class QuitCommand extends AbstractCommand implements Command
{
    public static final String USER_NOT_REMOVED = "User wasn't removed! Please try again!";
    public static final String SERVER_DISCONNECT = "You are disconnected from server!";

    public QuitCommand()
    {
        this.commandName = "quit";
        this.isBroadcastCommand = true;
    }

    public Context parse(String command, String args)
    {
        Context context = new Context();

        HashMap<String, String> keys = new HashMap<String, String>();
        keys.put("command", command);

        context.setKeys(keys);
        return context;
    }

    public void action(Context context)
    {
        User user = context.getUser();

        if(UserRegistry.getInstance().removeUser(Long.toString(user.getId()), user.getLogin()))
        {
            //context.setUser(null);
            context.setCode(Response.STATUS_SUCCESS_QUIT); // quit
            context.setCommandWasExecuted(true);
            context.setThreadClose(true);
            context.setMessage(SERVER_DISCONNECT);
        }
        else
        {
            context.setCode(Response.STATUS_FAIL_QUIT);
            context.setErrorMessage(USER_NOT_REMOVED);
        }
    }
}
