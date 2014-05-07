package org.vfs.server.command.impl;

import org.vfs.server.command.Command;
import org.vfs.server.model.Context;
import org.vfs.server.model.impl.Directory;
import org.vfs.server.user.User;

import java.util.HashMap;

/**
 * @author Lipatov Nikita
 */
public class PrintCommand extends AbstractCommand implements Command
{

    public PrintCommand()
    {
        this.commandName = "print";
        this.isBroadcastCommand = false;
    }

    public Context parse(String command, String args)
    {
        Context context = new Context();

        HashMap<String, String> keys = new HashMap<String, String>();
        keys.put("command", command);

        context.setKeys(keys);
        return context;
    }

    public void action(Context context)
    {
        User user = context.getUser();
        Directory directory = user.getDirectory();

        String tree = printTree(directory);
        context.setCommandWasExecuted(true);
        context.setMessage(tree);
    }
}
