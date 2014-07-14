package org.vfs.server.command.impl;

import org.vfs.core.network.protocol.User;
import org.vfs.core.command.Command;
import org.vfs.core.command.CommandValues;
import org.vfs.core.model.Context;
import org.vfs.server.model.Node;
import org.vfs.server.model.NodeFactory;
import org.vfs.server.model.impl.Directory;

/**
 * @author Lipatov Nikita
 */
public class MakeDirectoryCommand extends AbstractServerCommand implements Command
{
    public static final String DIRECTORY_NOT_CREATED = "Directory could not be created!";

    public MakeDirectoryCommand()
    {
        this.commandName = "mkdir";
    }

    public void action(Context context)
    {
        User user = context.getUser();
        Directory directory = (Directory)user.getDirectory();
        CommandValues values = context.getCommandValues();
        String createDirectory = values.getNextParam();

        Node node = search(directory, createDirectory);
        if(node == null)
        {
            Directory makeDirectory = NodeFactory.getFactory().createDirectory(directory, createDirectory);
            if(makeDirectory != null)
            {
                context.setCommandWasExecuted(true);
                context.setBroadcastCommand(true);
                context.setMessage("Directory " + makeDirectory.getFullPath() + " was created!");
                return;
            }
        }
        context.setErrorMessage(DIRECTORY_NOT_CREATED);
    }


}
