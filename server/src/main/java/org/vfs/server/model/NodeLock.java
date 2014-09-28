package org.vfs.server.model;

import org.vfs.core.network.protocol.User;

import java.util.concurrent.Semaphore;

/**
 * @author Lipatov Nikita
 */
public class NodeLock {

    private volatile User user;
    private final Semaphore semaphore; // or AtomicBoolean

    public NodeLock() {
        this.semaphore = new Semaphore(1);
    }

    public boolean isLocked() {
        int permitCount = semaphore.availablePermits();
        return (permitCount > 0) ? true : false;
    }

    public void acquire(User user) {
        try {
            this.user = user;
            semaphore.acquire();
        } catch (InterruptedException ie) {
            System.err.println(ie);
        }
    }

    public void release() {
        user = null;
        semaphore.release();
    }

    public User getUser() {
        return user;
    }

}
