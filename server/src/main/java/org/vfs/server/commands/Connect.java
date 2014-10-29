package org.vfs.server.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.core.command.CommandValues;
import org.vfs.core.exceptions.QuitException;
import org.vfs.core.network.protocol.proto.ResponseFactory;
import org.vfs.core.network.protocol.proto.ResponseProto;
import org.vfs.server.model.UserSession;
import org.vfs.server.services.NodeService;
import org.vfs.server.services.UserService;

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
            send(ResponseProto.Response.ResponseType.FAIL_CONNECT, "Such user already exits. Please, change the login!");

            throw new QuitException("Such user already exist!");
        } else {
            userService.attachUser(userSession.getUser().getId(), login);
            clientWriter.send( // send id from server to client
                    ResponseFactory.newResponse(
                            ResponseProto.Response.ResponseType.SUCCESS_CONNECT,
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
