package org.vfs.server.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.core.command.CommandValues;
import org.vfs.server.model.Node;
import org.vfs.server.model.NodeTypes;
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
@Component("copy")
public class Copy implements Command {

    private final NodeService nodeService;
    private final LockService lockService;
    private final UserService userService;

    @Autowired
    public Copy(NodeService nodeService, LockService lockService, UserService userService) {
        this.nodeService = nodeService;
        this.lockService = lockService;
        this.userService = userService;
    }

    @Override
    public void apply(UserSession userSession, CommandValues values) {
        ClientWriter clientWriter = userSession.getClientWriter();
        Node directory = userSession.getNode();
        String source = values.getNextParam();
        String destination = values.getNextParam();

        Node sourceNode = nodeService.getNode(directory, source);
        Node destinationNode = nodeService.getNode(directory, destination);

        if (sourceNode == null) {
            clientWriter.send(newResponse(STATUS_OK, "Source path/node is not found!"));
            return;
        }
        if (destinationNode == null) {
            clientWriter.send(newResponse(STATUS_OK, "Destination path/node is not found!"));
            return;
        }

        if (destinationNode.getType() == NodeTypes.DIR) {

            if (lockService.isLocked(destinationNode, true)) {
                clientWriter.send(newResponse(STATUS_OK, "Node or children nodes is/are locked!"));
                return;
            }

            Node copyNode = nodeService.clone(sourceNode);
            nodeService.getNodeManager().setParent(copyNode, destinationNode);
            clientWriter.send(
                    newResponse(
                            STATUS_OK,
                            getMessageToYou(sourceNode, destinationNode)
                    )
            );
            userService.sendMessageToUsers(
                    userSession.getUser().getId(),
                    getMessageToAll(userSession.getUser().getLogin(), sourceNode, destinationNode)
            );
        } else {
            clientWriter.send(
                    newResponse(
                            STATUS_OK,
                            "Destination path is not directory"
                    )
            );
        }
    }

    private String getMessageToYou(Node source, Node destination) {
        return "You has copied node by path '" + nodeService.getFullPath(source) + "' to destination node by path '" + nodeService.getFullPath(destination) + "' ";
    }

    private String getMessageToAll(String login, Node source, Node destination) {
        return "User '" + login + "' has copied node by path '" + nodeService.getFullPath(source) + "' to destination node by path '" + nodeService.getFullPath(destination) + "' ";
    }
}
