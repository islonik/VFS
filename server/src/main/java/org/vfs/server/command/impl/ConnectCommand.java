package org.vfs.server.command.impl;

import org.vfs.core.network.protocol.Response;
import org.vfs.server.command.Command;
import org.vfs.server.model.Context;
import org.vfs.server.user.User;
import org.vfs.server.user.UserRegistry;

/**
 * @author Lipatov Nikita
 */
public class ConnectCommand extends AbstractCommand implements Command
{
    public static final String USER_ALREADY_EXIST = "User with such login already was registered before. Please, change the login!";
    public static final String ADD_USER_FAIL = "User wasn't added and registered! Try to change user login and type connect command again!";
    public static final String CONNECT = "connect";

    public ConnectCommand()
    {
        this.commandName = CONNECT;
        this.isBroadcastCommand = true;
    }

    public void action(Context context)
    {
        String userName = context.getArg1();

        if(UserRegistry.getInstance().getUser(userName) != null)
        {
            context.setCode(Response.STATUS_FAIL_CONNECT);
            context.setErrorMessage(USER_ALREADY_EXIST);
            context.setThreadClose(true);
            return;
        }

        if(UserRegistry.getInstance().addUser(userName))
        {
            User user = UserRegistry.getInstance().getUser(userName);
            context.setUser(user);
            context.setCommandWasExecuted(true);
            context.setCode(Response.STATUS_SUCCESS_CONNECT);
            context.setSpecificCode(user.getId());
            context.setMessage(user.getDirectory().getFullPath());
        }
        else
        {
            context.setCode(Response.STATUS_FAIL_CONNECT);
            context.setErrorMessage(ADD_USER_FAIL);
            context.setThreadClose(true);
        }
    }
}
