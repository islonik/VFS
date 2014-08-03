package org.vfs.server.model;

import org.vfs.core.network.protocol.User;
import org.vfs.server.model.Node;

import java.net.Socket;
import java.util.*;

/**
 * @author Lipatov Nikita
 */
public class UserSession {
    private User user;
    private Node node;
    private Socket socket;

    public UserSession() {
    }

    public UserSession(User user, Node node, Socket socket) {
        this.user = user;
        setNode(node);
        this.socket = socket;
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
        if (this.node.getType() != NodeTypes.DIR) {
            throw new IllegalArgumentException("UserSession: Node is not DIR!");
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
