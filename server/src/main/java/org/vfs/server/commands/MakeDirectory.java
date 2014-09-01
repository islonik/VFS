package org.vfs.server.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.core.command.CommandValues;
import org.vfs.server.model.Node;
import org.vfs.server.model.NodeTypes;
import org.vfs.server.model.UserSession;
import org.vfs.server.network.ClientWriter;
import org.vfs.server.services.NodeService;

import static org.vfs.core.network.protocol.Response.STATUS_OK;
import static org.vfs.core.network.protocol.ResponseFactory.newResponse;

/**
 * @author Lipatov Nikita
 */
@Component("mkdir")
public class MakeDirectory implements Command {

    private final NodeService nodeService;

    @Autowired
    public MakeDirectory(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    public void apply(UserSession userSession, CommandValues values, ClientWriter clientWriter) {
        Node directory = userSession.getNode();
        String newNodeName = values.getNextParam();

        Node node = nodeService.getNode(directory, newNodeName);
        if (node == null) {
            node = nodeService.createNode(directory, newNodeName, NodeTypes.DIR);
            if(node != null) {
                clientWriter.send(newResponse(STATUS_OK, "Directory " + nodeService.getFullPath(node) + " was created!"));
            } else {
                clientWriter.send(newResponse(STATUS_OK, "Directory was not created!"));
            }
        } else {
            clientWriter.send(newResponse(STATUS_OK, "Directory could not be created!"));
        }
    }
}
