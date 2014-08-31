package org.vfs.server.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.core.command.CommandValues;
import org.vfs.core.network.protocol.User;
import org.vfs.server.model.Node;
import org.vfs.server.model.UserSession;
import org.vfs.server.network.ClientWriter;
import org.vfs.server.services.LockService;
import org.vfs.server.services.NodeService;

import static org.vfs.core.network.protocol.Response.STATUS_OK;
import static org.vfs.core.network.protocol.ResponseFactory.newResponse;

/**
 * @author Lipatov Nikita
 */
@Component("lock")
public class Lock implements Command {

    private final NodeService nodeService;
    private final LockService lockService;

    @Autowired
    public Lock(NodeService nodeService, LockService lockService) {
        this.nodeService = nodeService;
        this.lockService = lockService;
    }

    @Override
    public void apply(UserSession userSession, CommandValues values, ClientWriter clientWriter) {
        User user = userSession.getUser();
        Node directory = userSession.getNode();
        String key = values.getNextKey();
        String lockDirectory = values.getNextParam();

        Node node = nodeService.search(directory, lockDirectory);
        if (node != null) {
            boolean recursive = false;
            if(key != null && key.equals("r")) {
                recursive = true;
            }
            if (lockService.isLocked(node, recursive)) {
                clientWriter.send(newResponse(STATUS_OK, "Node or children nodes is/are locked!"));
                return;
            }
            lockService.lock(user, node, recursive);
            clientWriter.send(newResponse(STATUS_OK, "Node " + nodeService.getFullPath(node) + " was locked!"));
        } else {
            clientWriter.send(newResponse(STATUS_OK, "Destination node is not found!"));
        }
    }
}
