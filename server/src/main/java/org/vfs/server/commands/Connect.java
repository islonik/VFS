package org.vfs.server.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.core.command.CommandValues;
import org.vfs.server.model.UserSession;
import org.vfs.server.network.ClientWriter;
import org.vfs.server.services.NodeService;
import org.vfs.server.services.UserService;

import static org.vfs.core.network.protocol.Response.STATUS_FAIL_CONNECT;
import static org.vfs.core.network.protocol.Response.STATUS_SUCCESS_CONNECT;
import static org.vfs.core.network.protocol.ResponseFactory.newResponse;

/**
 * @author Lipatov Nikita
 */
@Component("connect")
public class Connect implements Command {

    private final UserService userService;
    private final NodeService nodeService;

    @Autowired
    public Connect(UserService userService, NodeService nodeService) {
        this.userService = userService;
        this.nodeService = nodeService;
    }

    @Override
    public void apply(UserSession userSession, CommandValues values, ClientWriter clientWriter) {
        String login = values.getNextParam();
        if (userService.isLogged(login)) {
            clientWriter.send(newResponse(STATUS_FAIL_CONNECT, "User was registered before with such login already. Please, change the login!"));
        } else {
            userService.attachUser(userSession.getUser().getId(), login);
            clientWriter.send(newResponse(STATUS_SUCCESS_CONNECT, nodeService.getFullPath(userSession.getNode()), userSession.getUser().getId()));
        }
    }
}
