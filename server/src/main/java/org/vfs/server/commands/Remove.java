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
@Component("rm")
public class Remove implements Command {

    private final NodeService nodeService;
    private final LockService lockService;
    private final UserService userService;

    @Autowired
    public Remove(NodeService nodeService, LockService lockService, UserService userService) {
        this.nodeService = nodeService;
        this.lockService = lockService;
        this.userService = userService;
    }

    @Override
    public void apply(UserSession userSession, CommandValues values) {
        ClientWriter clientWriter = userSession.getClientWriter();
        Node directory = userSession.getNode();
        String nodeName = values.getNextParam();

        Node node = nodeService.getNode(directory, nodeName);

        if (node != null) {
            if (lockService.isLocked(node, true)) {
                clientWriter.send(
                        newResponse(
                                STATUS_OK,
                                "Node or children nodes is / are locked!"
                        )
                );
                return;
            }

            boolean isRemoved = nodeService.removeNode(directory, nodeName);
            if (isRemoved) {
                clientWriter.send(
                        newResponse(
                                STATUS_OK,
                                "Node '" + nodeName + "' was deleted!"
                        )
                );
                userService.sendMessageToUsers(
                        userSession.getUser().getId(),
                        "Node '" + nodeName + "' was deleted by user '" + userSession.getUser().getLogin() + "'"
                );
            } else {
                clientWriter.send(
                        newResponse(
                                STATUS_OK,
                                "Node was found, but wasn't deleted!"
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
