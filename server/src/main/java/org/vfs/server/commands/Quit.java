package org.vfs.server.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.core.command.CommandValues;
import org.vfs.core.exceptions.QuitException;
import org.vfs.core.network.protocol.Protocol;
import org.vfs.server.model.UserSession;
import org.vfs.server.services.UserService;

/**
 * @author Lipatov Nikita
 */
@Component("quit")
public class Quit extends AbstractCommand implements Command {

    private final UserService userService;

    @Autowired
    public Quit(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void apply(UserSession userSession, CommandValues values) {
        clientWriter = userSession.getClientWriter();
        String login = userSession.getUser().getLogin();

        userService.stopSession(userSession.getUser().getId());

        send(Protocol.Response.ResponseType.SUCCESS_QUIT, "You are disconnected from server!");

        userService.notifyUsers(
                userSession.getUser().getId(),
                String.format("User '%s' has been disconnected", login)
        );

        throw new QuitException(
                String.format("User '%s' has been disconnected", login)
        );
    }

}
