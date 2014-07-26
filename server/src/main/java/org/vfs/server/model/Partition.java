package org.vfs.server.model;

/**
 * @author Lipatov Nikita
 */
public class Partition
{
    private Node root;

    public Partition(Node node)
    {
        if(node.getType() != NodeTypes.DIR)
        {
            throw new IllegalArgumentException("Node " + node.getName() + " is not directory!");
        }
        root = node;
    }

    public Node getRoot()
    {
        return root;
    }

}
