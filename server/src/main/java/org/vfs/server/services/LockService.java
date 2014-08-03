package org.vfs.server.services;

import org.vfs.core.network.protocol.User;
import org.vfs.server.model.Node;
import org.vfs.server.model.NodeLock;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Lipatov Nikita
 */
public class LockService {
    private final Map<Node, NodeLock> lockMap = new ConcurrentHashMap<>();

    public boolean addNode(Node node) {
        if (lockMap.containsKey(node)) {
            return false;
        }
        lockMap.put(node, new NodeLock());
        return true;
    }

    public boolean removeNode(Node node) {
        if (!lockMap.containsKey(node)) {
            return false;
        }
        lockMap.remove(node);
        return true;
    }

    public boolean isLocked(Node node) {
        if (lockMap.containsKey(node)) {
            return !lockMap.get(node).isLocked();
        }
        return false;
    }

    public boolean isLocked(Node node, boolean recursive) {
        return (recursive) ? isRecursiveLocked(node) : isLocked(node);
    }

    private boolean isRecursiveLocked(Node node) {
        if (lockMap.containsKey(node)) {
            Collection<Node> children = node.getChildren();
            for(Node child : children) {
                if(isRecursiveLocked(child)) {
                    return true;
                }
            }
            return !lockMap.get(node).isLocked();
        }
        return false;
    }

    public boolean lock(User user, Node node) {
        if (lockMap.containsKey(node)) {
            lockMap.get(node).acquire(user);
            return true;
        }
        return false;
    }

    public boolean lock(User user, Node node, boolean recursive) {
        return (recursive) ? recursiveLock(user, node) : lock(user, node);
    }

    private boolean recursiveLock(User user, Node node) {
        if (lockMap.containsKey(node)) {
            Collection<Node> children = node.getChildren();
            for (Node child : children) {
                recursiveLock(user, child);
            }
            lockMap.get(node).acquire(user);
            return true;
        }
        return false;
    }

    public boolean unlock(User user, Node node) {
        if (lockMap.containsKey(node) && isLocked(node)) {
            NodeLock nodeLock = lockMap.get(node);
            if (nodeLock.getUser().equals(user)) {
                nodeLock.release();
                return true;
            }
        }
        return false;
    }

    public void unlockAllNodes(User user) {
        Set<Node> nodes = lockMap.keySet();
        for(Node node : nodes) {
            NodeLock nodeLock = lockMap.get(node);
            if(nodeLock.getUser().equals(user)) {
                nodeLock.release();
            }
        }
    }

    public boolean unlock(User user, Node node, boolean recursive) {
        return (recursive) ? recursiveUnlock(user, node) : unlock(user, node);
    }

    private boolean recursiveUnlock(User user, Node node) {
        if (lockMap.containsKey(node) && isLocked(node)) {
            Collection<Node> children = node.getChildren();
            for (Node child : children) {
                recursiveUnlock(user, child);
            }
            NodeLock nodeLock = lockMap.get(node);
            if (nodeLock.getUser().equals(user)) {
                nodeLock.release();
                return true;
            }
        }
        return false;
    }

    public User getUser(Node node) {
        if (lockMap.containsKey(node)) {
            return lockMap.get(node).getUser();
        }
        return null;
    }


}
