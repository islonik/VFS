package org.vfs.server.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.core.command.CommandValues;
import org.vfs.server.exceptions.QuitException;
import org.vfs.server.model.UserSession;
import org.vfs.server.network.ClientWriter;
import org.vfs.server.services.LockService;
import org.vfs.server.services.UserService;

import static org.vfs.core.network.protocol.Response.STATUS_SUCCESS_QUIT;
import static org.vfs.core.network.protocol.ResponseFactory.newResponse;

/**
 * @author Lipatov Nikita
 */
@Component("quit")
public class Quit implements Command {

    private final LockService lockService;
    private final UserService userService;

    @Autowired
    public Quit(LockService lockService, UserService userService) {
        this.lockService = lockService;
        this.userService = userService;
    }

    @Override
    public void apply(UserSession userSession, CommandValues values, ClientWriter clientWriter) {
        String login = userSession.getUser().getLogin();
        lockService.unlockAll(userSession.getUser());
        userService.stopSession(userSession.getUser().getId());
        clientWriter.send(newResponse(STATUS_SUCCESS_QUIT, "You are disconnected from server!"));
        throw new QuitException(login);
    }
}
