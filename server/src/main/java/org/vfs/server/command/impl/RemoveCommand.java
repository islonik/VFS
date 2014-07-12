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
public class RemoveCommand extends AbstractServerCommand implements Command
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
        Directory directory = (Directory)user.getDirectory();
        CommandValues values = context.getCommandValues();
        String removeNode = values.getNextParam();

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
