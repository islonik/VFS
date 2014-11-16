package org.vfs.server.model;

import org.vfs.core.network.protocol.Protocol;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Lipatov Nikita
 */
public class NodeLock {

    private volatile Protocol.User user;
    private final Lock lock;

    public NodeLock() {
        lock = new ReentrantLock();
    }

    public boolean isLocked() {
        return (user == null) ? false : true;
    }

    public void lock(Protocol.User user) {
        boolean result = lock.tryLock();
        if(result) {
            this.user = user;
        }
    }

    public void unlock() {
        user = null;
        lock.unlock();
    }

    public Protocol.User getUser() {
        return user;
    }

}
