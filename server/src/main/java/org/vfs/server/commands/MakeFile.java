package org.vfs.server.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.core.command.CommandValues;
import org.vfs.server.model.Node;
import org.vfs.server.model.NodeTypes;
import org.vfs.server.model.UserSession;
import org.vfs.server.network.ClientWriter;
import org.vfs.server.services.NodeService;
import org.vfs.server.services.UserService;

import static org.vfs.core.network.protocol.Response.STATUS_OK;
import static org.vfs.core.network.protocol.ResponseFactory.newResponse;

/**
 * @author Lipatov Nikita
 */
@Component("mkfile")
public class MakeFile implements Command {

    private final NodeService nodeService;
    private final UserService userService;

    @Autowired
    public MakeFile(NodeService nodeService, UserService userService) {
        this.nodeService = nodeService;
        this.userService = userService;
    }

    @Override
    public void apply(UserSession userSession, CommandValues values) {
        ClientWriter clientWriter = userSession.getClientWriter();
        Node directory = userSession.getNode();
        String createNode = values.getNextParam();

        Node node = nodeService.getNode(directory, createNode);
        if (node == null) {
            node = nodeService.createNode(directory, createNode, NodeTypes.FILE);
            if(node != null) {
                clientWriter.send(
                        newResponse(
                                STATUS_OK,
                                "New file '" + nodeService.getFullPath(node) + "' was created!"
                        )
                );
                userService.sendMessageToUsers(
                        userSession.getUser().getId(),
                        "New file '" + nodeService.getFullPath(node) + "' was created by user '" + userSession.getUser().getLogin() + "'"
                );
            } else {
                clientWriter.send(
                        newResponse(
                                STATUS_OK,
                                "New file was not created!"
                        )
                );
            }
        } else {
            clientWriter.send(
                    newResponse(
                            STATUS_OK,
                            "New file could not be created!"
                    )
            );
        }
    }
}
