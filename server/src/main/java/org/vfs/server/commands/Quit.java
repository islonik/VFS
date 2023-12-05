package org.vfs.server.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.core.command.CommandValues;
import org.vfs.core.exceptions.QuitException;
import org.vfs.core.network.protocol.Protocol.Response;
import org.vfs.server.model.UserSession;
import org.vfs.server.services.UserSessionService;

/**
 * @author Lipatov Nikita
 */
@Component("quit")
public class Quit extends AbstractCommand implements Command {

    private final UserSessionService userSessionService;

    @Autowired
    public Quit(UserSessionService userSessionService) {
        this.userSessionService = userSessionService;
    }

    @Override
    public void apply(UserSession userSession, CommandValues values) {
        clientWriter = userSession.getClientWriter();
        String login = userSession.getUser().getLogin();

        userSessionService.stopSession(userSession.getUser().getId());

        send(Response.ResponseType.SUCCESS_QUIT, "You are disconnected from server!");

        userSessionService.notifyUsers(
                userSession.getUser().getId(),
                String.format("User '%s' has been disconnected", login)
        );

        throw new QuitException(
                String.format("User '%s' has been disconnected", login)
        );
    }

}
