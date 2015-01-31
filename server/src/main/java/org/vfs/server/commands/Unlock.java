package org.vfs.server.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.core.command.CommandValues;
import org.vfs.core.network.protocol.Protocol;
import org.vfs.server.model.Node;
import org.vfs.server.model.UserSession;
import org.vfs.server.services.LockService;
import org.vfs.server.services.NodeService;
import org.vfs.server.services.UserSessionService;

/**
 * @author Lipatov Nikita
 */
@Component("unlock")
public class Unlock extends AbstractCommand implements Command {

    private final NodeService nodeService;
    private final LockService lockService;
    private final UserSessionService userSessionService;

    @Autowired
    public Unlock(NodeService nodeService, LockService lockService, UserSessionService userSessionService) {
        this.nodeService = nodeService;
        this.lockService = lockService;
        this.userSessionService = userSessionService;
    }

    @Override
    public void apply(UserSession userSession, CommandValues values) {
        clientWriter = userSession.getClientWriter();
        Protocol.User user = userSession.getUser();
        Node directory = userSession.getNode();
        String key = values.getNextKey();
        String unlockDirectory = values.getNextParam();

        Node node = nodeService.getNode(directory, unlockDirectory);
        if (node != null) {
            boolean recursive = false;
            if (key != null && key.equals("r")) {
                recursive = true;
            }

            if (!lockService.isLocked(node, recursive)) {
                sendFail("Node is already unlocked!");
                return;
            }
            if (lockService.unlock(user, node, recursive)) {
                sendOK(String.format("Node '%s' was unlocked!", nodeService.getFullPath(node)));

                userSessionService.notifyUsers(
                        userSession.getUser().getId(),
                        String.format("Node '%s' was unlocked by user '%s'", nodeService.getFullPath(node), user.getLogin())
                );
            } else {
                sendOK("Node is locked by different user!");
            }
        } else {
            sendOK("Node is not found!");
        }
    }
}
