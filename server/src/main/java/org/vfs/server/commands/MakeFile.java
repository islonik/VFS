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
@Component("mkfile")
@RequiredArgsConstructor
public class MakeFile extends AbstractCommand implements Command {

    private final NodeService nodeService;
    private final UserSessionService userSessionService;

    @Override
    public void apply(UserSession userSession, CommandValues values) {
        clientWriter = userSession.getClientWriter();
        Node directory = userSession.getNode();
        String createNode = values.getNextParam();

        Node node = nodeService.getNode(directory, createNode);
        if (node == null) {
            node = nodeService.createNode(directory, createNode, NodeTypes.FILE);
            if(node != null) {
                sendOK(String.format("New file '%s' was created!", nodeService.getFullPath(node)));
                userSessionService.notifyUsers(
                        userSession.getUser().getId(),
                        String.format("New file '%s' was created by user '%s'", nodeService.getFullPath(node), userSession.getUser().getLogin())
                );
            } else {
                sendFail("New file was not created!");
            }
        } else {
            sendFail("New file could not be created!");
        }
    }
}
