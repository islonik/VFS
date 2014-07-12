package org.vfs.server.command.impl;

import org.vfs.core.network.protocol.User;
import org.vfs.core.command.Command;
import org.vfs.core.command.CommandValues;
import org.vfs.core.model.Context;
import org.vfs.server.model.Node;
import org.vfs.server.model.impl.Directory;

/**
 * @author Lipatov Nikita
 */
public class LockCommand extends AbstractServerCommand implements Command
{
    public static final String NODE_NOT_FOUND  = "Node not found!";
    public static final String NODE_NOT_LOCKED = "Node not locked!";

    public LockCommand()
    {
        this.commandName = "lock";
        this.isBroadcastCommand = true;
    }

    public void action(Context context)
    {
        User user = context.getUser();
        Directory directory = (Directory)user.getDirectory();
        CommandValues values = context.getCommandValues();
        String lockDirectory = values.getNextParam();

        Node node = search(directory, lockDirectory);
        if(node != null)
        {
            node.setLock(user, true);
            if(node.isLock(user))
            {
                context.setCommandWasExecuted(true);
                context.setMessage("Node " + node.getFullPath() + " was locked!");
                return;
            }
            else
            {
                context.setErrorMessage(NODE_NOT_LOCKED);
                return;
            }
        }
        context.setErrorMessage(NODE_NOT_FOUND);
    }
}
