package org.vfs.server.command.impl;

import org.vfs.server.command.Command;
import org.vfs.server.model.Context;
import org.vfs.server.model.Node;
import org.vfs.server.model.NodeFactory;
import org.vfs.server.model.impl.Directory;
import org.vfs.server.model.impl.File;
import org.vfs.server.user.User;

/**
 * @author Lipatov Nikita
 */
public class MakeFileCommand extends AbstractCommand implements Command
{
    public static final String FILE_NOT_CREATED = "File could not be created!";

    public MakeFileCommand()
    {
        this.commandName = "mkfile";
        this.isBroadcastCommand = true;
    }

    public void action(Context context)
    {
        User user = context.getUser();
        Directory directory = user.getDirectory();
        String createFile = context.getArg1();

        Node node = search(directory, createFile);
        if(node == null)
        {
            File makeFile = NodeFactory.getFactory().createFile(directory, createFile);
            if(makeFile != null)
            {
                context.setCommandWasExecuted(true);
                context.setMessage("File " + makeFile.getFullPath() + " was created!");
                return;
            }
        }
        context.setErrorMessage(FILE_NOT_CREATED);
    }
}
