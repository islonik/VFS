package org.vfs.server.model.impl;

import org.vfs.server.model.Node;

import java.util.*;

/**
 * @author Lipatov Nikita
 */
public class Directory extends AbstractNode implements Node
{

	private NodeList nodes = new NodeList();

	{
		this.type = NodeType.DIR;
	}

	public Directory(String name)
    {
		super(name);
	}
	
	public Directory(Directory parent, String name)
    {
		super(parent, name);
	}

    public Directory(Node node)
    {
        super(node);
    }

    public Node getNode(String name)
    {
        return nodes.search(name);
    }

    public List<Directory> getDirectories()
    {
        return nodes.getDirectories();
    }

    public List<File> getFiles()
    {
        return nodes.getFiles();
    }

	public boolean addNode(Node node)
    {
		node.setParent(this);
		return nodes.add(node);
	}
	
	public boolean removeNode(Node node)
    {
		node.setParent(null);
		return nodes.remove(node);      		
	}

    public boolean removeNode(Directory dir, String path)
    {
        if(path.contains("/"))
        {
            String name = path.substring(0, path.indexOf("/"));
            Directory pathDirectory;
            if(dir.getNode(name) == null)
            {
                return false;
            }
            else
            {
                pathDirectory = (Directory)dir.getNode(name);
                if(pathDirectory.isLock())
                {
                    return false;
                }
            }

            path = path.substring(path.indexOf("/") + 1, path.length());
            return dir.removeNode(pathDirectory, path);
        }
        else
        {
            Node leafNode;

            if(dir.getNode(path) == null)
            {
                return false;
            }
            else
            {
                leafNode = dir.getNode(path);
                dir.removeNode(leafNode);
            }
            return true;
        }
    }

    public Node copy()
    {
        HashMap<String, Node> nodes = this.nodes.getNodes();
        Set<String> keysSet = nodes.keySet();
        Iterator<String> keysIterator = keysSet.iterator();
        Directory copyInstance = new Directory(this);
        while(keysIterator.hasNext())
        {
            String name = keysIterator.next();
            Node node = nodes.get(name);
            Node copyNode = node.copy();
            copyInstance.addNode(copyNode);
        }
        return copyInstance;
    }

    public Node search(String node)
    {
        if(nodes.getNodes().containsKey(node))
        {
            return nodes.getNodes().get(node);
        }
        return null;
    }



}
