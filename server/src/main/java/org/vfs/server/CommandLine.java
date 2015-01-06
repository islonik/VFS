package org.vfs.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vfs.core.command.CommandParser;
import org.vfs.core.command.CommandValues;
import org.vfs.core.network.protocol.Protocol;
import org.vfs.core.network.protocol.ResponseFactory;
import org.vfs.server.commands.Command;
import org.vfs.server.network.ClientWriter;
import org.vfs.server.model.UserSession;

import java.util.Map;

/**
 * @author Lipatov Nikita
 */
public class CommandLine {
    private static final Logger log = LoggerFactory.getLogger(CommandLine.class);

    private final Map<String, Command> commands;
    private volatile ClientWriter clientWriter;

    private final CommandParser parser;

    public CommandLine(Map<String, Command> commands) {
        this.commands = commands;

        parser = new CommandParser();
    }

    public void onUserInput(UserSession userSession, Protocol.Request request) {
        clientWriter = userSession.getClientWriter();

        String fullCommand = request.getCommand();
        parser.parse(fullCommand);
        CommandValues commandValues = parser.getCommandValues();

        String command = commandValues.getCommand();

        try {
            if (commands.containsKey(command)) {
                commands.get(command).apply(userSession, commandValues);
            } else {
                clientWriter.send(
                        ResponseFactory.newResponse(
                                Protocol.Response.ResponseType.OK,
                                "No such command! Please check you syntax or type 'help'!"
                        )
                );
            }
        } catch(IllegalArgumentException e) {
            clientWriter.send(
                    ResponseFactory.newResponse(
                            Protocol.Response.ResponseType.FAIL,
                            e.getMessage()
                    )
            );
        } catch(IllegalAccessError e) {
            clientWriter.send(
                    ResponseFactory.newResponse(
                            Protocol.Response.ResponseType.FAIL,
                            e.getMessage()
                    )
            );
        } catch (NullPointerException npe) {
            clientWriter.send(
                    ResponseFactory.newResponse(
                            Protocol.Response.ResponseType.FAIL,
                            npe.getMessage()
                    )
            );
        }
    }

}

