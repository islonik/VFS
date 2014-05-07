package org.vfs.server.command.impl;

import org.vfs.server.command.Command;
import org.vfs.server.model.Context;
import org.vfs.server.model.Node;
import org.vfs.server.model.impl.Directory;
import org.vfs.server.user.User;

/**
 * @author Lipatov Nikita
 */
public class RemoveCommand extends AbstractCommand implements Command
{
    public static final String NODE_NOT_FOUND = "Node could not be found!";
    public static final String NODES_IS_LOCKED = "Node/nodes is/are locked! Please, unlock node/nodes and try again!";
    public static final String NODE_NOT_REMOVED = "Node could not be removed!";

    public RemoveCommand()
    {
        this.commandName = "rm";
        this.isBroadcastCommand = true;
    }

    public void action(Context context)
    {
        User user = context.getUser();
        Directory directory = user.getDirectory();
        String removeNode = context.getArg1();

        Node node = search(directory, removeNode);

        if(node == null)
        {
            context.setErrorMessage(NODE_NOT_FOUND);
            return;
        }
        else
        {
            boolean isLocksWereFound = searchLocks(node);
            if(isLocksWereFound)
            {
                context.setErrorMessage(NODES_IS_LOCKED);
                return;
            }
            boolean result = directory.removeNode(directory, removeNode);
            context.setCommandWasExecuted(result);
            context.setMessage("Node " + removeNode + " was removed!");
            if(!result)   // TODO: rewrite it!
            {
                context.setErrorMessage(NODE_NOT_REMOVED);
            }
        }
    }
}
