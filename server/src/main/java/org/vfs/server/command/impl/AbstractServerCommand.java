package org.vfs.server.command.impl;

import org.vfs.core.command.AbstractCommand;
import org.vfs.server.model.Node;
import org.vfs.server.model.impl.Directory;
import org.vfs.server.model.impl.File;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lipatov Nikita
 */
public abstract class AbstractServerCommand extends AbstractCommand
{
    public boolean searchLocks(Node node)
    {

        if(node instanceof File)
        {
            if(node.isLock())
            {
                return true;
            }
            return false;
        }
        else
        {
            Directory rootDirectory = (Directory) node;
            if(rootDirectory.isLock())
            {
                return true;
            }
            List<Directory> directories = rootDirectory.getDirectories();
            for(Directory directory : directories)
            {
                if(searchLocks(directory))
                {
                    return true;
                }
            }
            List<File> files = rootDirectory.getFiles();
            for(File file : files)
            {
                if(file.isLock())
                {
                    return true;
                }
            }
            return false;
        }
    }

    public Node search(Directory rootDirectory, String path)
    {
        Node node = null;
        if(path.contains("/"))
        {
            if(path.contains("/"))
            {
                String name = path.substring(0, path.indexOf("/"));
                if(name.isEmpty())
                {
                    return null;
                }

                if(name.equals(".."))
                {
                    node = rootDirectory.getParent();
                }
                else
                {
                    node = rootDirectory.getNode(name);
                }
            }
        }
        else
        {
            if(path.equals("."))
            {
                return rootDirectory;
            }
            else if(path.equals(".."))
            {
                return rootDirectory.getParent();
            }

            if(rootDirectory == null)
            {
                return null;
            }
            return rootDirectory.getNode(path);
        }

        if(node instanceof File)
        {
            return node;
        }
        else
        {
            path = path.substring(path.indexOf("/") + 1, path.length());
            return search((Directory) node, path);
        }
    }

    private List<Node> searchAll(Directory rootDirectory, String name)
    {
        List<Node> resultList = new ArrayList<Node>();
        List<Directory> directories = rootDirectory.getDirectories();
        for(Directory directory : directories)
        {
            if(directory.getName().startsWith(name))
            {
                resultList.add(directory);
            }
            resultList.addAll(searchAll(directory, name));
        }
        List<File> files = rootDirectory.getFiles();
        for(File file : files)
        {
            if(file.getName().startsWith(name))
            {
                resultList.add(file);
            }
        }
        return resultList;
    }


}
