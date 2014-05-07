package org.vfs.server.command.impl;

import org.vfs.server.command.Command;
import org.vfs.server.model.Context;

import java.util.HashMap;

/**
 * @author Lipatov Nikita
 */
public class HelpCommand extends AbstractCommand implements Command
{

    public static final String HELP_MESSAGE =
        "You can use next commands:\n" +
        "    * - connect server_name:port login \n" +
        "    * - quit \n" +
        "    * - cd directory \n" +
        "    * - copy node directory \n" +
        "    * - help \n" +
        "    * - lock node \n" +
        "    * - mkdir directory \n" +
        "    * - mkfile file\n" +
        "    * - move node directory \n" +
        "    * - print \n" +
        "    * - rm node \n" +
        "    * - unlock node ";

    public HelpCommand()
    {
        this.commandName = "help";
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
        context.setCommandWasExecuted(true);
        context.setMessage(HELP_MESSAGE);
    }
}
