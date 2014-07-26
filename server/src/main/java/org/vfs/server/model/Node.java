package org.vfs.server.model;

/**
 * @author Lipatov
 * @date 11.02.14
 */
public class Node {

    protected NodeTypes type;
    protected Node parent;
    protected String name;

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
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
