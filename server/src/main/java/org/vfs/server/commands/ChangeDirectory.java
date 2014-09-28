package org.vfs.server.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.core.command.CommandValues;
import org.vfs.server.model.Node;
import org.vfs.server.model.NodeTypes;
import org.vfs.server.model.UserSession;
import org.vfs.server.services.NodeService;

/**
 * @author Lipatov Nikita
 */
@Component("cd")
public class ChangeDirectory extends AbstractCommand implements Command {
    private final NodeService nodeService;

    @Autowired
    public ChangeDirectory(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void apply(UserSession userSession, CommandValues values) {
        clientWriter = userSession.getClientWriter();
        Node directory = userSession.getNode();
        String source = values.getNextParam();

        if (source == null) {
            source = ".";
        }

        Node node = nodeService.getNode(directory, source);
        if (node != null) {
            if (node.getType() == NodeTypes.FILE) {
                sendFail("Source node is file!");
            } else {
                userSession.setNode(node);
                sendOK(nodeService.getFullPath(node));
            }
        } else {
            sendFail("Destination node is not found!");
        }
    }
}
