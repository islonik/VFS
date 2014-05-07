package org.vfs.server.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vfs.server.model.Context;
import org.vfs.server.user.User;

import java.util.HashMap;

/**
 * @author Lipatov Nikita
 */
public class CommandLine
{
    private static final Logger log = LoggerFactory.getLogger(CommandLine.class);

    public static final String NO_SUCH_COMMAND = "Such command doesn't exist! Please use help command for getting full list of available commands!";

    public Context toContext(User user, String args)
    {
        try
        {
            args = trimSlashes(removeDoubleSlashes(args.toLowerCase().trim()));
            String commandName = (args.contains(" "))
                    ? args.substring(0, args.indexOf(" "))
                    : args.substring(0, args.length());
            if(commandName.length() + 1 <= args.length())
            {
                args = args.substring(commandName.length() + 1, args.length()).trim();
            }

            Context context;
            if(CommandMapping.getCommandMapping().containsKey(commandName))
            {
                Command command = CommandMapping.getCommandMapping().get(commandName);
                context = command.parse(commandName, args);
                context.setBroadcastCommand(command.isBroadcastCommand());
            }
            else
            {
                context = new Context();
            }
            if(user != null)
            {
                context.setUser(user);
            }
            return context;
        }
        catch(Exception e)
        {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public void execute(Context context)
    {
        synchronized (CommandLine.class)
        {
            HashMap<String, Command> mapping = CommandMapping.getCommandMapping();
            if(mapping.containsKey(context.getCommand()))
            {
                Command command = mapping.get(context.getCommand());
                command.action(context);
            }
            else
            {
                context.setErrorMessage(NO_SUCH_COMMAND);
            }
        }
    }

    public static String removeDoubleSlashes(String path)
    {
        if(path.contains("//"))
        {
            path = path.replaceAll("//", "/");
            return removeDoubleSlashes(path);
        }
        return path;
    }

    public static String trimSlashes(String path)
    {
        if(path.startsWith("/"))
        {
            path = path.substring(1, path.length());
        }

        if(path.endsWith("/"))
        {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }
}
