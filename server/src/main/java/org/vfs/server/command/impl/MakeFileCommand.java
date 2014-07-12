package org.vfs.server.command.impl;

import org.vfs.core.network.protocol.User;
import org.vfs.core.command.Command;
import org.vfs.core.command.CommandValues;
import org.vfs.core.model.Context;
import org.vfs.server.model.Node;
import org.vfs.server.model.NodeFactory;
import org.vfs.server.model.impl.Directory;
import org.vfs.server.model.impl.File;

/**
 * @author Lipatov Nikita
 */
public class MakeFileCommand extends AbstractServerCommand implements Command
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
        Directory directory = (Directory)user.getDirectory();
        CommandValues values = context.getCommandValues();
        String createFile = values.getNextParam();

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
