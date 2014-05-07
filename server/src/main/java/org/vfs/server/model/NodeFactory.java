package org.vfs.server.model;

import org.vfs.server.model.impl.Directory;
import org.vfs.server.model.impl.File;

/**
 * @author Lipatov Nikita
 */
public class NodeFactory
{

    private static NodeFactory factory = new NodeFactory();

    public static NodeFactory getFactory()
    {
        return factory;
    }

    public Directory createDirectory(String path)
    {
        return createDirectory(null, path);
    }

    public Directory createDirectory(Directory root, String path)
    {
        if(root == null)
        {
            if(path.contains("/"))
            {
                return createDirectory(Tree.getInstance().getRoot(), path);
            }
            else
            {
                return new Directory(path);
            }
        }
        if(path.contains("/"))
        {
            String name = path.substring(0, path.indexOf("/"));
            Directory pathDirectory;
            if(root.getNode(name) == null)
            {
                pathDirectory = new Directory(name);
                root.addNode(pathDirectory);
            }
            else
            {
                pathDirectory = (Directory)root.getNode(name);
            }

            path = path.substring(path.indexOf("/") + 1, path.length());
            return this.createDirectory(pathDirectory, path);
        }
        else
        {
            Directory leafDirectory;

            if(root.getNode(path) == null)
            {
                leafDirectory = new Directory(path);
                root.addNode(leafDirectory);
            }
            else
            {
                leafDirectory = (Directory)root.getNode(path);
            }
            return leafDirectory;
        }
    }

    public File createFile(String name)
    {
        return createFile(null, name);
    }

    public File createFile(Directory root, String path)
    {
        if(root == null)
        {
            if(path.contains("/"))
            {
                return createFile(Tree.getInstance().getRoot(), path);
            }
            else
            {
                return new File(path);
            }
        }
        if(path.contains("/"))
        {
            String name = path.substring(0, path.indexOf("/"));
            Directory pathDirectory;
            if(root.getNode(name) == null)
            {
                pathDirectory = new Directory(name);
                root.addNode(pathDirectory);
            }
            else
            {
                pathDirectory = (Directory)root.getNode(name);
            }

            path = path.substring(path.indexOf("/") + 1, path.length());
            return this.createFile(pathDirectory, path);
        }
        else
        {
            File leafFile;

            if(root.getNode(path) == null)
            {
                leafFile = new File(path);
                root.addNode(leafFile);
            }
            else
            {
                leafFile = (File)root.getNode(path);
            }
            return leafFile;
        }
    }
}
