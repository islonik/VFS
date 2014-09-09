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

import static org.vfs.core.network.protocol.Response.STATUS_OK;
import static org.vfs.core.network.protocol.ResponseFactory.newResponse;

/**
 * @author Lipatov Nikita
 */
@Component("move")
public class Move implements Command {

    private final NodeService nodeService;
    private final LockService lockService;

    @Autowired
    public Move(NodeService nodeService, LockService lockService) {
        this.nodeService = nodeService;
        this.lockService = lockService;
    }

    @Override
    public void apply(UserSession userSession, CommandValues values, ClientWriter clientWriter) {
        Node directory = userSession.getNode();
        String source = values.getNextParam();
        String destination = values.getNextParam();

        Node sourceNode = nodeService.getNode(directory, source);
        Node destinationNode = nodeService.getNode(directory, destination);

        if (sourceNode == null) {
            clientWriter.send(newResponse(STATUS_OK, "Source path/node not found!"));
            return;
        }

        if (destinationNode == null) {
            clientWriter.send(newResponse(STATUS_OK, "Destination path/node not found!"));
            return;
        }

        if (destinationNode.getType() == NodeTypes.DIR) {
            if (lockService.isLocked(destinationNode, true)) {
                clientWriter.send(newResponse(STATUS_OK, "Node or children nodes is/are locked!"));
                return;
            }

            Node parent = sourceNode.getParent();
            nodeService.setParent(sourceNode, destinationNode);
            nodeService.removeNode(parent, sourceNode);

            clientWriter.send(newResponse(STATUS_OK,
                    "Source node " + nodeService.getFullPath(sourceNode) + " was moved to destination node " + nodeService.getFullPath(destinationNode)));
        } else {
            clientWriter.send(newResponse(STATUS_OK, "Destination path is not directory"));
        }
    }
}