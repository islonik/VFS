package org.vfs.server.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.core.command.CommandValues;
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
@Component("rename")
public class Rename implements Command {

    private final NodeService nodeService;
    private final LockService lockService;
    private final UserService userService;

    @Autowired
    public Rename(NodeService nodeService, LockService lockService, UserService userService) {
        this.nodeService = nodeService;
        this.lockService = lockService;
        this.userService = userService;
    }

    @Override
    public void apply(UserSession userSession, CommandValues values) {
        ClientWriter clientWriter = userSession.getClientWriter();
        Node directory = userSession.getNode();
        String renameNode = values.getNextParam();
        String newName = values.getNextParam();

        Node node = nodeService.getNode(directory, renameNode);
        String oldName = node.getName();

        if (node != null) {
            if (lockService.isLocked(node, false)) {
                clientWriter.send(
                        newResponse(
                                STATUS_OK,
                                "Node is locked!"
                        )
                );
                return;
            }

            node.setName(newName);

            clientWriter.send(
                    newResponse(
                            STATUS_OK,
                            "Node '" + oldName + "' was renamed to '" + newName + "'"
                    )
            );
            userService.sendMessageToUsers(
                    userSession.getUser().getId(),
                    "Node '" + oldName + "' was renamed to '" + newName + "' by user '" + userSession.getUser().getLogin() + "'"
            );
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
