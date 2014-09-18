package org.vfs.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.vfs.server.aspects.NodeModifier;
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

    private Node root;
    private Node home;

    @Autowired
    public NodeService(@Value("${delimiter}")String separator, LockService lockService) {
        if (separator.length() != 1) {
            throw new IllegalArgumentException("Separator should consist from one symbol!");
        }
        this.separator = separator;
        this.lockService = lockService;
    }

    @NodeModifier
    public Node newNode(String name, NodeTypes type) {
        Node node = new Node(name, type);
        System.out.println("newNode!");
        lockService.addNode(node);
        return node;
    }

    public Node clone(Node source) {
        Node clone = newNode(source.getName(), source.getType());
        Collection<Node> children = source.getChildren();
        for (Node child : children) {
            Node copyChild = clone(child);
            copyChild.setParent(clone);
        }
        return clone;
    }

    public void setParent(Node node, Node parent) {
        if (duplicateExist(node, parent)) {
            throw new IllegalArgumentException("Name already exist for " + parent.getName());
        }
        node.setParent(parent);
    }

    public boolean removeNode(Node source, Node child) {
        if (source.removeChild(child)) {
            setParent(child, null);
            return removeLock(source, child);
        }
        return false;
    }

    private boolean removeLock(Node source, Node child) {
        if (source.contains(child)) {
            Collection<Node> nodes = child.getChildren();
            for (Node node : nodes) {
                removeLock(child, node);
            }
            lockService.removeNode(child);
            return true;
        }
        return false;
    }

    private boolean duplicateExist(Node node, Node parent) {
        if (parent == null) {
            return false;
        }
        for (Node child : parent.getChildren()) {
            if (child.getName().equals(node.getName())) {
                return true;
            }
        }
        return false;
    }

    public Node getRoot() {
        return root;
    }

    public Node getHome() {
        return home;
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
                directoryNode = this.newNode(directoryName, NodeTypes.DIR);
                setParent(directoryNode, root);
            } else {
                if (directoryNode.getType() == NodeTypes.FILE) {
                    throw new IllegalArgumentException("You try create node through file node!");
                }
            }

            return this.createNode(directoryNode, path, nodeType);
        } else {
            if(lockService.isLocked(root)) {
                throw new IllegalAccessError("Parent node " + getFullPath(root) + " is locked! Please wait until this node will be unlocked!");
            }
            Node leafNode = findByName(root, path);

            if (leafNode == null) {
                leafNode = this.newNode(path, nodeType);
                setParent(leafNode, root);
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
                parent.removeChild(leafNode);
            }
            return true;
        }
    }

    public void initDirs() {
        root = this.newNode("/", NodeTypes.DIR);
        home = this.newNode("home", NodeTypes.DIR);
        setParent(home, root);
    }
}
