package org.vfs.client.command;

import org.vfs.client.command.impl.DefaultCommand;

import org.vfs.core.command.AbstractCommandLine;
import org.vfs.core.command.Command;
import org.vfs.core.command.CommandValues;
import org.vfs.core.model.Context;
import org.vfs.core.network.protocol.User;

import java.util.HashMap;

/**
 * @author Lipatov Nikita
 */
public class CommandLine extends AbstractCommandLine
{

    private ClientMapping clientMapping = new ClientMapping();

    public Context execute(User user, String args)
    {
        args = trimSlashes(removeDoubleSlashes(args.toLowerCase().trim()));

        Context context = new Context();
        context.setUser(user);
        context.setCommand(args);

        CommandValues commandValues = context.getCommandValues();

        // connect, quit and exit commands
        HashMap<String, Command> mapping = clientMapping.getMapping();
        if(mapping.containsKey(commandValues.getCommand()))
        {
            Command command = mapping.get(commandValues.getCommand());
            command.action(context);
        }
        // other commands
        else
        {
            DefaultCommand defaultCommand = new DefaultCommand();
            defaultCommand.action(context);
        }

        return context;
    }
}
