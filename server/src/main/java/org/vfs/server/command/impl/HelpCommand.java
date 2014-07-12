package org.vfs.server.command.impl;

import org.vfs.core.command.Command;
import org.vfs.core.model.Context;

/**
 * @author Lipatov Nikita
 */
public class HelpCommand extends AbstractServerCommand implements Command
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

    public void action(Context context)
    {
        context.setCommandWasExecuted(true);
        context.setMessage(HELP_MESSAGE);
    }
}
