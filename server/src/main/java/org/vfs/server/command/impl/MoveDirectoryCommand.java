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
public class MoveDirectoryCommand extends AbstractServerCommand implements Command
{
    public static final String SOURCE_NOT_FOUND = "Source path/node not found!";
    public static final String DESTINATION_NOT_FOUND = "Destination path/node not found!";
    public static final String DESTINATION_NOT_DIRECTORY = "Destination path is not directory";

    public MoveDirectoryCommand()
    {
        this.commandName = "move";
    }

    public void action(Context context)
    {
        User user = context.getUser();
        Directory directory = (Directory)user.getDirectory();
        CommandValues values = context.getCommandValues();
        String source      = values.getNextParam();
        String destination = values.getNextParam();

        Node sourceNode      = search(directory, source);
        Node destinationNode = search(directory, destination);

        if(sourceNode == null)
        {
            context.setErrorMessage(SOURCE_NOT_FOUND);
            return;
        }

        if(destinationNode == null)
        {
            context.setErrorMessage(DESTINATION_NOT_FOUND);
            return;
        }

        if(destinationNode instanceof Directory)
        {
            Directory parent = sourceNode.getParent();
            ((Directory) destinationNode).addNode(sourceNode);
            parent.removeNode(sourceNode);
            context.setCommandWasExecuted(true);
            context.setBroadcastCommand(true);
            context.setMessage("Source node " + sourceNode.getFullPath() + " was moved to destination node " + destinationNode.getFullPath());
        }
        else
        {
            context.setErrorMessage(DESTINATION_NOT_DIRECTORY);
        }

    }
}
