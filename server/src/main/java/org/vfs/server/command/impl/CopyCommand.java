package org.vfs.server.command.impl;

import org.vfs.server.command.Command;
import org.vfs.server.model.Context;
import org.vfs.server.model.Node;
import org.vfs.server.model.impl.Directory;
import org.vfs.server.user.User;

/**
 * @author Lipatov Nikita
 */
public class CopyCommand extends AbstractCommand implements Command
{
    public static final String SOURCE_NOT_FOUND = "Source path/node not found!";
    public static final String DESTINATION_NOT_FOUND = "Destination path/node not found!";
    public static final String DESTINATION_NOT_DIRECTORY = "Destination path is not directory";

    public CopyCommand()
    {
        this.commandName = "copy";
        this.isBroadcastCommand = true;
    }

    public Context parse(String command, String args)
    {
        return this.twoArgs(command, args);
    }

    public void action(Context context)
    {
        User user = context.getUser();
        Directory directory = user.getDirectory();
        String source = context.getArg1();
        String destination = context.getArg2();

        Node sourceNode = search(directory, source);
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
            Node copyNode = sourceNode.copy();
            ((Directory) destinationNode).addNode(copyNode);
            context.setCommandWasExecuted(true);
            context.setMessage("Source node " + sourceNode.getFullPath() + " was copied to destination node " + destinationNode.getFullPath() );
        }
        else
        {
            context.setErrorMessage(DESTINATION_NOT_DIRECTORY);
        }

    }


}
