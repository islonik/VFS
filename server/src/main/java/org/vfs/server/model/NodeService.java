package org.vfs.server.model;

/**
 * @author Lipatov Nikita
 */
public class NodeService
{
    private Partition partition;
    private String separator;

    public NodeService(Partition partition, String separator)
    {
        if(partition.getRoot() == null)
        {
            throw new IllegalArgumentException("Partition should contain root node!");
        }
        if(separator.length() != 1)
        {
            throw new IllegalArgumentException("Separator should consist from one symbol!");
        }
        this.partition = partition;
        this.separator = separator;
    }

    public String removeDoubleSeparators(String path)
    {
        if(path.contains(separator + separator))
        {
            path = path.replaceAll(separator + separator , separator);
            return removeDoubleSeparators(path);
        }
        return path;
    }

    public String getFullPath(Node node)
    {
        String path = null;
        String result = node.getName();
        if(node.getParent() != null)
        {
            path = getFullPath(node.getParent());
        }
        if(path != null)
        {
            result = path + separator + result;
        }
        return removeDoubleSeparators(partition.getRoot().getName() + separator + result);
    }

    public Node createHomeDirectory(String login)
    {
        // TODO: implement it!!
        return null;
    }



}
