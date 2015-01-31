package org.vfs.server.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.core.command.CommandValues;
import org.vfs.server.model.Node;
import org.vfs.server.model.UserSession;
import org.vfs.server.services.LockService;
import org.vfs.server.services.NodeService;
import org.vfs.server.services.UserSessionService;

/**
 * @author Lipatov Nikita
 */
@Component("rename")
public class Rename extends AbstractCommand implements Command {

    private final NodeService nodeService;
    private final LockService lockService;
    private final UserSessionService userSessionService;

    @Autowired
    public Rename(NodeService nodeService, LockService lockService, UserSessionService userSessionService) {
        this.nodeService = nodeService;
        this.lockService = lockService;
        this.userSessionService = userSessionService;
    }

    @Override
    public void apply(UserSession userSession, CommandValues values) {
        clientWriter = userSession.getClientWriter();
        Node directory = userSession.getNode();
        String renameNode = values.getNextParam();
        String newName = values.getNextParam();

        Node node = nodeService.getNode(directory, renameNode);
        String oldName = node.getName();

        if (node != null) {
            if (lockService.isLocked(node, false)) {
                sendFail("Node is locked!");
                return;
            }

            node.setName(newName);

            sendOK(String.format("Node '%s' was renamed to '%s'", oldName, newName));

            userSessionService.notifyUsers(
                    userSession.getUser().getId(),
                    String.format("Node '%s' was renamed to '%s' by user '%s'", oldName, newName, userSession.getUser().getLogin())
            );
        } else {
            sendFail("Node is not found!");
        }
    }
}
