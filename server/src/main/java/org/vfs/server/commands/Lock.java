package org.vfs.server.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.core.command.CommandValues;
import org.vfs.core.network.protocol.Protocol;
import org.vfs.server.model.Node;
import org.vfs.server.model.UserSession;
import org.vfs.server.services.LockService;
import org.vfs.server.services.NodeService;
import org.vfs.server.services.UserService;

/**
 * @author Lipatov Nikita
 */
@Component("lock")
public class Lock extends AbstractCommand implements Command {

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
        clientWriter = userSession.getClientWriter();
        Protocol.User user = userSession.getUser();
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
                sendOK("Node or children nodes is/are locked!");
                return;
            }
            lockService.lock(user, node, recursive);

            sendOK(String.format("You has locked the node by path '%s'", nodeService.getFullPath(node)));

            userService.notifyUsers(
                    userSession.getUser().getId(),
                    String.format("User %s has locked the node by path '%s'", user.getLogin(), nodeService.getFullPath(node))
            );
        } else {
            sendFail("Destination node is not found!");
        }
    }
}
