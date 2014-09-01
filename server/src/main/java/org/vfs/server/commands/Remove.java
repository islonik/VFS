package org.vfs.server.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.core.command.CommandValues;
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
@Component("rm")
public class Remove implements Command {

    private final NodeService nodeService;
    private final LockService lockService;

    @Autowired
    public Remove(NodeService nodeService, LockService lockService) {
        this.nodeService = nodeService;
        this.lockService = lockService;
    }

    @Override
    public void apply(UserSession userSession, CommandValues values, ClientWriter clientWriter) {
        Node directory = userSession.getNode();
        String nodeName = values.getNextParam();

        Node node = nodeService.getNode(directory, nodeName);

        if (node != null) {
            if (lockService.isLocked(node, true)) {
                clientWriter.send(newResponse(STATUS_OK, "Node or children nodes is / are locked!"));
                return;
            }

            boolean isRemoved = nodeService.removeNode(directory, nodeName);
            if (isRemoved) {
                clientWriter.send(newResponse(STATUS_OK, "Node " + nodeName + " was deleted!"));
            } else {
                clientWriter.send(newResponse(STATUS_OK, "Node was found, but wasn't deleted!"));
            }
        } else {
            clientWriter.send(newResponse(STATUS_OK, "Node is not found!"));
        }
    }
}
