package org.vfs.server.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.core.command.CommandValues;
import org.vfs.server.model.Node;
import org.vfs.server.model.UserSession;
import org.vfs.server.services.LockService;
import org.vfs.server.services.NodeService;
import org.vfs.server.services.UserService;

/**
 * @author Lipatov Nikita
 */
@Component("rm")
public class Remove extends AbstractCommand implements Command {

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
        clientWriter = userSession.getClientWriter();
        Node directory = userSession.getNode();
        String nodeName = values.getNextParam();

        Node node = nodeService.getNode(directory, nodeName);

        if (node != null) {
            if (lockService.isLocked(node, true)) {
                sendFail("Node or children nodes is / are locked!");
                return;
            }

            boolean isRemoved = nodeService.removeNode(directory, nodeName);
            if (isRemoved) {
                sendOK(String.format("Node '%s' was deleted!", nodeName));

                userService.notifyUsers(
                        userSession.getUser().getId(),
                        String.format("Node '%s' was deleted by user '%s'", userSession.getUser().getLogin())
                );
            } else {
                sendFail("Node was found, but wasn't deleted!");
            }
        } else {
            sendFail("Node is not found!");
        }
    }
}
