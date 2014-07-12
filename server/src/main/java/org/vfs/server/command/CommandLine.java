package org.vfs.server.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vfs.core.command.AbstractCommandLine;
import org.vfs.core.command.Command;
import org.vfs.core.command.CommandParser;
import org.vfs.core.command.CommandValues;
import org.vfs.core.network.protocol.User;
import org.vfs.core.model.Context;

import java.util.HashMap;

/**
 * @author Lipatov Nikita
 */
public class CommandLine extends AbstractCommandLine
{
    private static final Logger log = LoggerFactory.getLogger(CommandLine.class);

    public static final String NO_SUCH_COMMAND = "Such command doesn't exist! Please use help command for getting full list of available commands!";

    private ServerMapping serverMapping = new ServerMapping();

    public Context execute(User user, String args)
    {
        synchronized (CommandLine.class)
        {
            args = trimSlashes(removeDoubleSlashes(args.toLowerCase().trim()));
            CommandParser parser = new CommandParser();
            parser.parse(args);

            CommandValues commandValues = parser.getCommandValues();

            Context context = new Context();
            context.setUser(user);
            context.setCommandValues(commandValues);

            HashMap<String, Command> mapping = serverMapping.getMapping();
            if(mapping.containsKey(commandValues.getCommand()))
            {
                Command command = mapping.get(commandValues.getCommand());
                command.action(context);
                context.setBroadcastCommand(command.isBroadcastCommand());
            }
            else
            {
                context.setErrorMessage(NO_SUCH_COMMAND);
            }

            return context;
        }
    }

}
