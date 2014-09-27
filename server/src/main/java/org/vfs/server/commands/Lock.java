package org.vfs.server.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.core.command.CommandValues;
import org.vfs.core.network.protocol.User;
import org.vfs.server.model.Node;
import org.vfs.server.model.UserSession;
import org.vfs.server.network.ClientWriter;
import org.vfs.server.services.LockService;
import org.vfs.server.services.NodeService;
import org.vfs.server.services.UserService;

import static org.vfs.core.network.protocol.Response.STATUS_OK;
import static org.vfs.core.network.protocol.ResponseFactory.newResponse;

/**
 * @author Lipatov Nikita
 */
@Component("lock")
public class Lock implements Command {

    private final NodeService nodeService;
    private final LockService lockService;
    private final UserService userService;

    @Autowired
    public Lock(NodeService nodeService, LockService lockService, UserService userService) {
        this.nodeService = nodeService;
        this.lockService = lockService;
        this.userService = userService;
    }

    @Override
    public void apply(UserSession userSession, CommandValues values) {
        ClientWriter clientWriter = userSession.getClientWriter();
        User user = userSession.getUser();
        Node directory = userSession.getNode();
        String key = values.getNextKey();
        String lockDirectory = values.getNextParam();

        Node node = nodeService.getNode(directory, lockDirectory);
        if (node != null) {
            boolean recursive = false;
            if (key != null && key.equals("r")) {
                recursive = true;
            }
            if (lockService.isLocked(node, recursive)) {
                clientWriter.send(
                        newResponse(
                                STATUS_OK,
                                "Node or children nodes is/are locked!"
                        )
                );
                return;
            }
            lockService.lock(user, node, recursive);
            clientWriter.send(
                    newResponse(
                            STATUS_OK,
                            "You has locked the node by path '" + nodeService.getFullPath(node) + "'!"
                    )
            );
            userService.sendMessageToUsers(
                    userSession.getUser().getId(),
                    "User '" + userSession.getUser().getLogin() + "' has locked the node by path '" + nodeService.getFullPath(node) + "'"
            );
        } else {
            clientWriter.send(
                    newResponse(
                            STATUS_OK,
                            "Destination node is not found!"
                    )
            );
        }
    }
}
