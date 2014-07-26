package org.vfs.server.user;

import org.vfs.core.network.protocol.User;
import org.vfs.server.model.Node;
import org.vfs.server.model.NodeService;

/**
 * @author Lipatov Nikita
 */
public class UserService
{
    private GeneratorID generator;
    private NodeService nodeService;

    public UserService(GeneratorID generator, NodeService nodeService)
    {
        this.generator = generator;
        this.nodeService = nodeService;
    }

    public UserCell createCell(String login)
    {
        User user = new User();
        user.setId(generator.getNextId());
        user.setLogin(login);

        UserCell userCell = new UserCell();
        userCell.setUser(user);
        Node home = nodeService.createHomeDirectory(login);
        userCell.setNode(home);
        return userCell;
    }


}
