package org.vfs.server.model;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.vfs.core.network.protocol.Protocol.User;

/**
 * @author Lipatov Nikita
 */
public class NodeLock {

    private volatile User user;
    private final Lock lock;

    public NodeLock() {
        lock = new ReentrantLock();
    }

    public boolean isLocked() {
        return (user == null) ? false : true;
    }

    public void lock(User user) {
        boolean result = lock.tryLock();
        if(result) {
            this.user = user;
        }
    }

    public void unlock() {
        user = null;
        lock.unlock();
    }

    public User getUser() {
        return user;
    }

}
