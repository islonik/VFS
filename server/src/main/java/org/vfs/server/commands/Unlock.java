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
@Component("unlock")
public class Unlock implements Command {

    private final NodeService nodeService;
    private final LockService lockService;
    private final UserService userService;

    @Autowired
    public Unlock(NodeService nodeService, LockService lockService, UserService userService) {
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
        String unlockDirectory = values.getNextParam();

        Node node = nodeService.getNode(directory, unlockDirectory);
        if (node != null) {
            boolean recursive = false;
            if (key != null && key.equals("r")) {
                recursive = true;
            }

            if (!lockService.isLocked(node, recursive)) {
                clientWriter.send(
                        newResponse(
                                STATUS_OK,
                                "Node is already unlocked!"
                        )
                );
                return;
            }
            if (lockService.unlock(user, node, recursive)) {
                clientWriter.send(
                        newResponse(
                                STATUS_OK,
                                "Node '" + nodeService.getFullPath(node) + "' was unlocked!"
                        )
                );
                userService.sendMessageToUsers(
                        userSession.getUser().getId(),
                        "Node '" + nodeService.getFullPath(node) + "' was unlocked by user '" + user.getLogin() + "'"
                );
            } else {
                clientWriter.send(
                        newResponse(
                                STATUS_OK,
                                "Node is locked by different user!"
                        )
                );
            }
        } else {
            clientWriter.send(
                    newResponse(
                            STATUS_OK,
                            "Node is not found!"
                    )
            );
        }
    }
}
