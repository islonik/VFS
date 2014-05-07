package org.vfs.server.command.impl;

import org.vfs.server.command.Command;
import org.vfs.server.model.Context;
import org.vfs.server.model.Node;
import org.vfs.server.model.impl.Directory;
import org.vfs.server.user.User;

/**
 * @author Lipatov Nikita
 */
public class UnlockCommand extends AbstractCommand implements Command
{
    public static final String NODE_NOT_FOUND = "Node not found!";
    public static final String NODE_LOCK_DIFF_USER = "Node is locked by different user!";
    public static final String NODE_ALREADY_UNLOCKED = "Node is already unlocked!";

    public UnlockCommand()
    {
        this.commandName = "unlock";
        this.isBroadcastCommand = true;
    }

    public void action(Context context)
    {
        User user = context.getUser();
        Directory directory = user.getDirectory();
        String unlockDirectory = context.getArg1();

        Node node = search(directory, unlockDirectory);
        if(node != null)
        {
            if(!node.isLock())
            {
                context.setErrorMessage(NODE_ALREADY_UNLOCKED);
                return;
            }
            node.setLock(user, false);
            if(!node.isLock())
            {
                context.setCommandWasExecuted(true);
                context.setMessage("Node " + node.getFullPath() + " was unlocked!");
            }
            else
            {
                context.setErrorMessage(NODE_LOCK_DIFF_USER);
            }
        }
        else
        {
            context.setErrorMessage(NODE_NOT_FOUND);
        }
    }
}
