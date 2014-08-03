package org.vfs.server.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Lipatov
 * @date 11.02.14
 */
public class Node implements Comparable<Node> {

    protected NodeTypes type;
    protected Node parent;
    protected String name;
    protected List<Node> children = new ArrayList<Node>();

    public Node(String name, NodeTypes type) {
        this.name = name;
        this.type = type;
    }

    public NodeTypes getType() {
        return type;
    }

    public void setType(NodeTypes type) {
        this.type = type;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        if (parent == null) {
            this.parent = parent;
            return;
        }
        if (parent.getType() != NodeTypes.DIR) {
            throw new IllegalArgumentException("Node has different type than NodeTypes.DIR");
        }
        this.parent = parent;
        parent.children.add(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Node> getChildren() {
        return Collections.unmodifiableCollection(children);
    }

    public boolean contains(Node node) {
        return children.contains(node);
    }

    public boolean removeChild(Node node) {
        return children.remove(node);
    }

    public int compareTo(Node node) {
        return name.compareTo(node.getName());
    }

}
