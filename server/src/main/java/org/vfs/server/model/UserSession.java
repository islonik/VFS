package org.vfs.server.model;

import org.vfs.core.network.protocol.User;
import org.vfs.server.network.ClientWriter;

import java.net.Socket;
import java.util.concurrent.Future;

/**
 * @author Lipatov Nikita
 */
public class UserSession {
    private volatile User user;
    private volatile Node node;
    private volatile Socket socket;
    private volatile Timer timer;
    private volatile Future task;
    private volatile ClientWriter clientWriter;
    private volatile boolean isAuth;

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

    public final Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public final Future getTask() {
        return task;
    }

    public void setTask(Future task) {
        this.task = task;
    }

    public final Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public final ClientWriter getClientWriter() {
        return clientWriter;
    }

    public void setClientWriter(ClientWriter clientWriter) {
        this.clientWriter = clientWriter;
    }

    public boolean isAuth() {
        return isAuth;
    }

    public void setAuth(boolean isAuth) {
        this.isAuth = isAuth;
    }
}
