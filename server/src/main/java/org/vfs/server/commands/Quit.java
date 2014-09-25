package org.vfs.server.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.core.command.CommandValues;
import org.vfs.core.exceptions.QuitException;
import org.vfs.server.model.UserSession;
import org.vfs.server.network.ClientWriter;
import org.vfs.server.services.UserService;

import static org.vfs.core.network.protocol.Response.STATUS_SUCCESS_QUIT;
import static org.vfs.core.network.protocol.ResponseFactory.newResponse;

/**
 * @author Lipatov Nikita
 */
@Component("quit")
public class Quit implements Command {

    private final UserService userService;

    @Autowired
    public Quit(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void apply(UserSession userSession, CommandValues values, ClientWriter clientWriter) {
        String login = userSession.getUser().getLogin();

        userService.stopSession(userSession.getUser().getId());

        clientWriter.send(newResponse(STATUS_SUCCESS_QUIT, "You are disconnected from server!"));
        throw new QuitException("User " + login + " has been disconnected!");
    }

}
