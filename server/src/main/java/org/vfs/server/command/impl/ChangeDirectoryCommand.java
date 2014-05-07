package org.vfs.server.command.impl;

import org.vfs.server.command.Command;
import org.vfs.server.model.Context;
import org.vfs.server.model.Node;
import org.vfs.server.model.impl.Directory;
import org.vfs.server.model.impl.File;
import org.vfs.server.user.User;

/**
 * @author Lipatov Nikita
 */
public class ChangeDirectoryCommand extends AbstractCommand implements Command
{
    public static final String NODE_IS_FILE = "Source node is file!";
    public static final String NODE_NOT_FOUND = "Directory wasn't found!";

    public ChangeDirectoryCommand()
    {
        this.commandName = "cd";
        this.isBroadcastCommand = false;
    }

    public void action(Context context)
    {
        User user = context.getUser();
        Directory directory = user.getDirectory();
        String source = context.getArg1();

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
