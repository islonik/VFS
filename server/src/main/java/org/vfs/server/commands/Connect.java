package org.vfs.server.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.core.command.CommandValues;
import org.vfs.core.exceptions.QuitException;
import org.vfs.server.model.UserSession;
import org.vfs.server.services.NodeService;
import org.vfs.server.services.UserService;

import static org.vfs.core.network.protocol.Response.STATUS_FAIL_CONNECT;
import static org.vfs.core.network.protocol.Response.STATUS_SUCCESS_CONNECT;
import static org.vfs.core.network.protocol.ResponseFactory.newResponse;

/**
 * @author Lipatov Nikita
 */
@Component("connect")
public class Connect extends AbstractCommand implements Command {

    private final NodeService nodeService;
    private final UserService userService;

    @Autowired
    public Connect(NodeService nodeService, UserService userService) {
        this.nodeService = nodeService;
        this.userService = userService;
    }

    @Override
    public void apply(UserSession userSession, CommandValues values) {
        clientWriter = userSession.getClientWriter();
        String login = values.getNextParam();
        if (userService.isLogged(login)) {
            send(STATUS_FAIL_CONNECT, "Such user already exits. Please, change the login!");

            throw new QuitException("Such user already exist!");
        } else {
            userService.attachUser(userSession.getUser().getId(), login);
            clientWriter.send( // send id from server to client
                    newResponse(
                            STATUS_SUCCESS_CONNECT,
                            nodeService.getFullPath(userSession.getNode()),
                            userSession.getUser().getId()
                    )
            );
            userService.notifyUsers(
                    userSession.getUser().getId(),
                    "User '" + login + "' has connected to server!"
            );
        }
    }
}
