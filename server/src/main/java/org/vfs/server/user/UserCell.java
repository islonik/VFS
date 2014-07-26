package org.vfs.server.user;

import org.vfs.core.network.protocol.User;
import org.vfs.server.model.Node;

/**
 * @author Lipatov Nikita
 */
public class UserCell {

    private User user;
    private Node node;

    public UserCell() {
    }

    public UserCell(User user, Node node) {
        this.user = user;
        this.node = node;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }
}
