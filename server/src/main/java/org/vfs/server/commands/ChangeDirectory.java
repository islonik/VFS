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
@Component("cd")
public class ChangeDirectory implements Command {
    private final NodeService nodeService;

    @Autowired
    public ChangeDirectory(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void apply(UserSession userSession, CommandValues values, ClientWriter clientWriter) {
        Node directory = userSession.getNode();
        String source = values.getNextParam();

        if (source == null) {
            source = ".";
        }

        Node node = nodeService.getNode(directory, source);
        if(node != null) {
            if (node.getType() == NodeTypes.FILE) {
                clientWriter.send(newResponse(STATUS_OK, "Source node is file!"));
            } else {
                userSession.setNode(node);
                clientWriter.send(newResponse(STATUS_OK, nodeService.getFullPath(node)));
            }
        } else {
            clientWriter.send(newResponse(STATUS_OK, "Destination node is not found!"));
        }
    }
}
