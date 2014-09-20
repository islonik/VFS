package org.vfs.server.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.core.command.CommandValues;
import org.vfs.core.exceptions.QuitException;
import org.vfs.core.network.protocol.User;
import org.vfs.server.model.Node;
import org.vfs.server.model.UserSession;
import org.vfs.server.network.ClientWriter;
import org.vfs.server.services.LockService;
import org.vfs.server.services.NodeService;
import org.vfs.server.services.UserService;

import java.util.Collection;

import static org.vfs.core.network.protocol.Response.STATUS_SUCCESS_QUIT;
import static org.vfs.core.network.protocol.ResponseFactory.newResponse;

/**
 * @author Lipatov Nikita
 */
@Component("quit")
public class Quit implements Command {

    private final LockService lockService;
    private final UserService userService;
    private final NodeService nodeService;

    @Autowired
    public Quit(LockService lockService, UserService userService, NodeService nodeService) {
        this.lockService = lockService;
        this.userService = userService;
        this.nodeService = nodeService;
    }

    @Override
    public void apply(UserSession userSession, CommandValues values, ClientWriter clientWriter) {
        String login = userSession.getUser().getLogin();

        this.removeUserHomeDirectory(login);

        lockService.unlockAll(userSession.getUser());
        userService.stopSession(userSession.getUser().getId());

        clientWriter.send(newResponse(STATUS_SUCCESS_QUIT, "You are disconnected from server!"));
        throw new QuitException("User " + login + " has been disconnected!");
    }

    private void removeUserHomeDirectory(String login) {
        Node home = nodeService.getHome();
        Node userHomeDir = nodeService.findByName(home, login);
        if(lockService.isLocked(userHomeDir, true)) {
            Collection<Node> lockingNodes= lockService.getAllLockedNodes(userHomeDir);
            for(Node lockingNode : lockingNodes) {
                User lockingUser = lockService.getUser(lockingNode);
                lockService.unlock(lockingUser, userHomeDir);
            }
        }
        nodeService.removeNode(home, login);
    }
}
