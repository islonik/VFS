package org.vfs.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.vfs.core.network.protocol.User;
import org.vfs.server.model.Node;
import org.vfs.server.model.NodeTypes;

import java.util.*;

/**
 * @author Lipatov Nikita
 */
@Component
public class NodeService {
    private String separator;
    private final LockService lockService;
    private final NodeManager nodeManager;

    private Node root;
    private Node home;

    @Autowired
    public NodeService(@Value("${delimiter}")String separator, LockService lockService, NodeManager nodeManager) {
        if (separator.length() != 1) {
            throw new IllegalArgumentException("Separator should consist from one symbol!");
        }
        this.separator = separator;
        this.lockService = lockService;
        this.nodeManager = nodeManager;
    }

    public Node clone(Node source) {
        Node clone = nodeManager.newNode(source.getName(), source.getType());
        Collection<Node> children = source.getChildren();
        for (Node child : children) {
            Node copyChild = clone(child);
            copyChild.setParent(clone);
        }
        return clone;
    }

    public Node getRoot() {
        return root;
    }

    public Node getHome() {
        return home;
    }

    public NodeManager getNodeManager() {
        return nodeManager;
    }

    public String removeDoubleSeparators(String path) {
        if (path.contains(separator + separator)) {
            path = path.replaceAll(separator + separator, separator);
            return removeDoubleSeparators(path);
        }
        return path;
    }

    public String getFullPath(Node node) {
        String path = null;
        String result = node.getName();
        if (node.getParent() != null) {
            path = getFullPath(node.getParent());
        }
        if (path != null) {
            result = path + separator + result;
        }
        return removeDoubleSeparators(separator + result);
    }

    public Node findByName(Node root, String path) {
        path = path.trim();
        if (path.equals(".") || path.isEmpty()) {
            return root;
        } else if (path.equals("..")) {
            return root.getParent();
        }
        Collection<Node> children = root.getChildren();
        for (Node child : children) {
            if (child.getName().equals(path)) {
                return child;
            }
        }
        return null;
    }

    public Node createNode(Node root, String path, NodeTypes nodeType) {
        if (path.contains(separator)) {
            String directoryName = path.substring(0, path.indexOf(separator));
            path = path.substring(path.indexOf(separator) + 1, path.length());
            if (directoryName.isEmpty()) {
                return createNode(root, path, nodeType);
            }

            Node directoryNode = findByName(root, directoryName);

            if (directoryNode == null) {
                directoryNode = nodeManager.newNode(directoryName, NodeTypes.DIR);
                nodeManager.setParent(directoryNode, root);
            } else {
                if (directoryNode.getType() == NodeTypes.FILE) {
                    throw new IllegalArgumentException("You try to create the node through file node!");
                }
            }

            return this.createNode(directoryNode, path, nodeType);
        } else {
            if(lockService.isLocked(root)) {
                throw new IllegalAccessError("Parent node " + getFullPath(root) + " is locked! Please wait until this node will be unlocked!");
            }
            Node leafNode = findByName(root, path);

            if (leafNode == null) {
                leafNode = nodeManager.newNode(path, nodeType);
                nodeManager.setParent(leafNode, root);
            }
            return leafNode;
        }
    }

    public Node getNode(Node root, String path) {
        if(path == null){
            return null;
        }
        if (path.contains(separator)) {
            String directoryName = path.substring(0, path.indexOf(separator));
            if (directoryName.isEmpty()) {
                directoryName = path.substring(path.indexOf(separator) + 1, path.length());
                return getNode(root, directoryName);
            }
            Node directoryNode = findByName(root, directoryName);

            if (directoryNode == null) {
                return null;
            }

            if (directoryNode.getType() == NodeTypes.FILE) {
                return directoryNode;
            } else {
                path = path.substring(path.indexOf(separator) + 1, path.length());
                return getNode(directoryNode, path);
            }
        } else {
            return findByName(root, path);
        }
    }

    public boolean removeNode(Node root, String path) {
        if (path.contains(separator)) {
            String directoryName = path.substring(0, path.indexOf(separator));
            path = path.substring(path.indexOf(separator) + 1, path.length());
            if (directoryName.isEmpty()) {
                return removeNode(root, path);
            }

            Node directoryNode = findByName(root, directoryName);

            if (directoryNode == null) {
                return false;
            }

            return this.removeNode(directoryNode, path);
        } else {
            Node leafNode = findByName(root, path);

            if (leafNode != null) {
                Node parent = leafNode.getParent();
                Collection<Node> removingNodes = getAllNodes(leafNode);
                removingNodes.add(leafNode);
                for(Node removingNode : removingNodes) {
                    nodeManager.removeNode(parent, removingNode);
                }
            }
            return true;
        }
    }

    public Collection<Node> getAllNodes(Node node) {
        Collection<Node> nodes = new ArrayList<>();
        for(Node child: node.getChildren()) {
            nodes.add(child);
            Collection<Node> children = getAllNodes(child);
            nodes.addAll(children);
        }
        return nodes;
    }

    public void initDirs() {
        if(root == null) {
            root = nodeManager.newNode("/", NodeTypes.DIR);
            home = nodeManager.newNode("home", NodeTypes.DIR);
            nodeManager.setParent(home, root);
        }
    }

    public Node createHomeDirectory(String login) {
        Node loginHome = nodeManager.newNode(login, NodeTypes.DIR);
        this.nodeManager.setParent(loginHome, home);
        return loginHome;
    }

    public void removeHomeDirectory(String login) {
        Node home = this.getHome();
        Node userHomeDir = this.findByName(home, login);
        if(lockService.isLocked(userHomeDir, true)) {
            Collection<Node> lockingNodes= lockService.getAllLockedNodes(userHomeDir);
            for(Node lockingNode : lockingNodes) {
                User lockingUser = lockService.getUser(lockingNode);
                lockService.unlock(lockingUser, userHomeDir);
            }
        }
        this.removeNode(home, login);
    }
}
