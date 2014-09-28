package org.vfs.server.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.core.command.CommandValues;
import org.vfs.server.model.Node;
import org.vfs.server.model.NodeTypes;
import org.vfs.server.model.UserSession;
import org.vfs.server.services.LockService;
import org.vfs.server.services.NodeService;
import org.vfs.server.services.UserService;

/**
 * @author Lipatov Nikita
 */
@Component("copy")
public class Copy extends AbstractCommand implements Command {

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
        clientWriter = userSession.getClientWriter();
        Node directory = userSession.getNode();
        String source = values.getNextParam();
        String destination = values.getNextParam();

        Node sourceNode = nodeService.getNode(directory, source);
        Node destinationNode = nodeService.getNode(directory, destination);

        if (sourceNode == null) {
            sendFail("Source path/node is not found!");
            return;
        }
        if (destinationNode == null) {
            sendFail("Destination path/node is not found!");
            return;
        }

        if (destinationNode.getType() == NodeTypes.DIR) {

            if (lockService.isLocked(destinationNode, true)) {
                sendOK("Node or children nodes is/are locked!");
                return;
            }

            Node copyNode = nodeService.clone(sourceNode);
            nodeService.getNodeManager().setParent(copyNode, destinationNode);
            sendOK(getMessageToYou(sourceNode, destinationNode));

            userService.notifyUsers(
                    userSession.getUser().getId(),
                    getMessageToAll(userSession.getUser().getLogin(), sourceNode, destinationNode)
            );
        } else {
            sendFail("Destination path is not directory");
        }
    }

    private String getMessageToYou(Node source, Node destination) {
        return String.format(
                "You has copied node by path '%s' to destination node by path '%s'",
                nodeService.getFullPath(source),
                nodeService.getFullPath(destination)
        );
    }

    private String getMessageToAll(String login, Node source, Node destination) {
        return String.format(
                "User '%s' has copied node by path '%s' to destination node by path '%s'",
                login,
                nodeService.getFullPath(source),
                nodeService.getFullPath(destination)
        );
    }
}
