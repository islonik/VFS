package org.vfs.server.command.impl;

import org.vfs.core.network.protocol.User;
import org.vfs.core.command.Command;
import org.vfs.core.command.CommandValues;
import org.vfs.core.model.Context;
import org.vfs.server.model.Node;
import org.vfs.server.model.impl.Directory;
import org.vfs.server.model.impl.File;

/**
 * @author Lipatov Nikita
 */
public class ChangeDirectoryCommand extends AbstractServerCommand implements Command
{
    public static final String NODE_IS_FILE = "Source node is file!";
    public static final String NODE_NOT_FOUND = "Directory wasn't found!";

    public ChangeDirectoryCommand()
    {
        this.commandName = "cd";
    }

    public void action(Context context)
    {
        User user = context.getUser();
        Directory directory = (Directory)user.getDirectory();

        CommandValues values = context.getCommandValues();
        String source = values.getNextParam();

        if(source == null)
        {
            source = ".";
        }

        Node node = search(directory, source);
        if(node instanceof File)
        {
            context.setErrorMessage(NODE_IS_FILE);
        }
        else
        {
            if(node != null)
            {
                user.setDirectory((Directory)node);
                context.setCommandWasExecuted(true);
                context.setMessage(node.getFullPath());
            }
            else
            {
                context.setErrorMessage(NODE_NOT_FOUND);
            }
        }

    }
}
