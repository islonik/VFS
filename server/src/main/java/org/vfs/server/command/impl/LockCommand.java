package org.vfs.server.command.impl;

import org.vfs.server.command.Command;
import org.vfs.server.model.Context;
import org.vfs.server.model.Node;
import org.vfs.server.model.impl.Directory;
import org.vfs.server.user.User;

/**
 * @author Lipatov Nikita
 */
public class LockCommand extends AbstractCommand implements Command
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
        Directory directory = user.getDirectory();
        String lockDirectory = context.getArg1();

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
