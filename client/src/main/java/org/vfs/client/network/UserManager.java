package org.vfs.client.network;

import org.vfs.core.network.protocol.Protocol;

/**
 *
 * @author Lipatov Nikita
 */
public class UserManager {
    private volatile Protocol.User user;

    public boolean isAuthorized() {
        return (user != null);
    }

    public Protocol.User getUser() {
        if(user == null) {
            synchronized (this) {
                while(user == null) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        System.err.println("NetworkManager.getSocket().IOException.Message=" + e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return user;
    }

    public void setUser(Protocol.User user) {
        this.user = user;
    }
}
