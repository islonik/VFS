package org.vfs.server.command.impl;

import org.vfs.server.command.Command;
import org.vfs.server.model.Context;
import org.vfs.server.model.Node;
import org.vfs.server.model.NodeFactory;
import org.vfs.server.model.impl.Directory;
import org.vfs.server.user.User;

/**
 * @author Lipatov Nikita
 */
public class MakeDirectoryCommand extends AbstractCommand implements Command
{
    public static final String DIRECTORY_NOT_CREATED = "Directory could not be created!";

    public MakeDirectoryCommand()
    {
        this.commandName = "mkdir";
        this.isBroadcastCommand = true;
    }

    public void action(Context context)
    {
        User user = context.getUser();
        Directory directory = user.getDirectory();
        String createDirectory = context.getArg1();

        Node node = search(directory, createDirectory);
        if(node == null)
        {
            Directory makeDirectory = NodeFactory.getFactory().createDirectory(directory, createDirectory);
            if(makeDirectory != null)
            {
                context.setCommandWasExecuted(true);
                context.setMessage("Directory " + makeDirectory.getFullPath() + " was created!");
                return;
            }
        }
        context.setErrorMessage(DIRECTORY_NOT_CREATED);
    }


}
