package org.vfs.server.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.vfs.core.command.CommandValues;
import org.vfs.server.model.Node;
import org.vfs.server.model.NodeTypes;
import org.vfs.server.model.UserSession;
import org.vfs.server.services.NodeService;
import org.vfs.server.services.UserSessionService;

/**
 * @author Lipatov Nikita
 */
@Component("mkdir")
@RequiredArgsConstructor
public class MakeDirectory extends AbstractCommand implements Command {

    private final NodeService nodeService;
    private final UserSessionService userSessionService;

    @Override
    public void apply(UserSession userSession, CommandValues values) {
        clientWriter = userSession.getClientWriter();
        Node directory = userSession.getNode();
        String newNodeName = values.getNextParam();

        Node node = nodeService.getNode(directory, newNodeName);
        if (node == null) {
            node = nodeService.createNode(directory, newNodeName, NodeTypes.DIR);
            if (node != null) {
                sendOK(String.format("New directory '%s' was created!", nodeService.getFullPath(node)));
                userSessionService.notifyUsers(
                        userSession.getUser().getId(),
                        String.format("New directory '%s' was created by user '%s'", nodeService.getFullPath(node), userSession.getUser().getLogin())
                );
            } else {
                sendFail("New directory was not created!");
            }
        } else {
            sendFail("New directory could not be created!");
        }
    }
}
