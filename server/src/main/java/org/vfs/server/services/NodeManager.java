package org.vfs.server.services;

import org.springframework.stereotype.Component;
import org.vfs.server.aspects.NewNodeModifier;
import org.vfs.server.aspects.RemoveNodeModifier;
import org.vfs.server.model.Node;
import org.vfs.server.model.NodeTypes;

/**
 * @author Lipatov Nikita
 */
@Component
public class NodeManager {

    public void setParent(Node node, Node parent) {
        if (duplicateExist(node, parent)) {
            throw new IllegalArgumentException("Name already exist for " + parent.getName());
        }
        node.setParent(parent);
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

    @NewNodeModifier
    public Node newNode(String name, NodeTypes type) {
        Node node = new Node(name, type);
        return node;
    }

    @RemoveNodeModifier
    public boolean removeNode(Node source, Node child) {
        if (source.removeChild(child)) {
            setParent(child, null);
            return true;
        }
        return false;
    }
}
