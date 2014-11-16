package org.vfs.client.network;

import org.vfs.core.network.protocol.Protocol;

import java.util.concurrent.CountDownLatch;

/**
 * CountDownLatch - notes:
 * When current thread try to get user(method getUser()) then the thread will stop until the other thread will set user(method setUser()).
 *
 * @author Lipatov Nikita
 */
public class UserManager {
    private CountDownLatch latch = new CountDownLatch(1);
    private volatile Protocol.User user;

    public boolean isAuthorized() {
        return (user != null);
    }

    public Protocol.User getUser() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return user;
    }

    public void setUser(Protocol.User user) {
        this.user = user;
        latch.countDown();
    }
}
