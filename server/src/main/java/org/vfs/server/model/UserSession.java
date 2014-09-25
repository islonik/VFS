package org.vfs.server.model;

import org.vfs.core.network.protocol.User;
import org.vfs.server.model.Node;

import java.net.Socket;
import java.util.*;
import java.util.concurrent.Future;

/**
 * @author Lipatov Nikita
 */
public class UserSession {
    private User user;
    private Node node;
    private Socket socket;
    private volatile Timer timer;
    private volatile Future task;

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

    public Future getTask() {
        return task;
    }

    public void setTask(Future task) {
        this.task = task;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }
}
