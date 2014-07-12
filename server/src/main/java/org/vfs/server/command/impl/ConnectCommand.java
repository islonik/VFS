package org.vfs.server.command.impl;

import org.vfs.core.network.protocol.Response;
import org.vfs.core.network.protocol.User;
import org.vfs.core.command.Command;
import org.vfs.core.command.CommandValues;
import org.vfs.core.model.Context;
import org.vfs.server.model.impl.Directory;
import org.vfs.server.user.UserRegistry;

/**
 * @author Lipatov Nikita
 */
public class ConnectCommand extends AbstractServerCommand implements Command
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
        CommandValues values = context.getCommandValues();
        String userName = values.getNextParam();

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
            context.setSpecificCode(Long.parseLong(user.getId()));
            context.setMessage(((Directory)user.getDirectory()).getFullPath());
        }
        else
        {
            context.setCode(Response.STATUS_FAIL_CONNECT);
            context.setErrorMessage(ADD_USER_FAIL);
            context.setThreadClose(true);
        }
    }
}
