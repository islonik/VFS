package org.vfs.server.model;

import org.vfs.server.model.impl.Directory;
import org.vfs.server.model.impl.File;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lipatov Nikita
 */
public class TreeStructure
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

    public String printTree(Directory directory)
    {
        return simplePrintTreeDir(directory, new StringBuilder(), 0).toString();
    }

    /**
     * The method prints tree on the screen.
     * @param parentDir Directory.
     * @param textTree String with nodes.
     * @param deep Depth from root.
     * @return
     */
    private StringBuilder simplePrintTreeDir(Directory parentDir, StringBuilder textTree, int deep)
    {
        simplePrintTreeNode(parentDir, textTree, deep);

        deep++;

        List<Directory> directories = parentDir.getDirectories();
        for (Directory directory : directories)
        {
            simplePrintTreeDir(directory, textTree, deep);
        }

        List<File> files = parentDir.getFiles();
        for (File file : files)
        {
            simplePrintTreeNode(file, textTree, deep);
        }

        return textTree;
    }

    /**
     * The method prints the files and their status.
     * @param node File.
     * @param textTree StringTree.
     * @param deep Depth from root.
     */
    private void simplePrintTreeNode(Node node, StringBuilder textTree, int deep)
    {
        if (deep != 0)
        {
            simplePrintHorizontalLine(textTree, deep);
        }
        textTree.append(node.getName());
        if (node.isLock())
        {
            textTree.append(" [Locked by ");
            textTree.append(node.getLockByUser().getLogin());
            textTree.append(" ]");
        }
        textTree.append("\n");
    }

    /**
     * The method prints horizontal string.
     * @param tree StringTree.
     * @param deep Depth from root.
     */
    private void simplePrintHorizontalLine(StringBuilder tree, int deep)
    {
        for (int i = 0; i < deep; i++)
        {
            tree.append("|");
            if (i + 1 != deep)
            {
                tree.append("  ");
            }
        }
        tree.append("__");
    }

}
