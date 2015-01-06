package org.vfs.server.model;

import org.vfs.core.network.protocol.Protocol;
import org.vfs.server.network.ClientWriter;

/**
 * @author Lipatov Nikita
 */
public class UserSession {
    private volatile Protocol.User user;
    private final Timer timer;
    private final ClientWriter clientWriter;

    private volatile Node node;

    public UserSession(Protocol.User user, ClientWriter clientWriter) {
        this.user = user;
        this.timer = new Timer();
        this.clientWriter = clientWriter;
    }

    public Protocol.User getUser() {
        return user;
    }

    public void setUser(Protocol.User user) {
        this.user = user;
    }

    public final Timer getTimer() {
        return timer;
    }

    public final ClientWriter getClientWriter() {
        return clientWriter;
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

}
