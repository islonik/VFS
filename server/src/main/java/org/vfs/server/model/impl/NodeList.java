package org.vfs.server.model.impl;

import org.vfs.server.model.Node;

import java.util.*;

/**
 * @author Lipatov Nikita
 */
public class NodeList
{
	
	private HashMap<String, Node> nodes = new HashMap<String, Node>();
	private List<Directory> directories = null;
	private List<File> files = null;
	
	private boolean changed = false;

    public NodeList()
    {
        directories = new ArrayList<Directory>();
        files = new ArrayList<File>();
    }

	public HashMap<String, Node> getNodes()
    {
		return nodes;
	}

	public void setNodes(HashMap<String, Node> nodes)
    {
		this.nodes = nodes;
	}

	public boolean add(Node node)
    {
		if(!nodes.containsKey(node.getName()))
        {
			nodes.put(node.getName(), node);
			changed = true;
			return true;
		}
        return false;
	}
	
	public boolean remove(Node node)
    {
		if(nodes.containsKey(node.getName()))
        {
			nodes.remove(node.getName());
			changed = true;
			return true;
		}
		return false;		
	}

    public Node search(String name)
    {
        if(nodes.containsKey(name))
        {
            return nodes.get(name);
        }
        return null;
    }
	
	public Node search(Node node)
    {
		if(nodes.containsKey(node.getName()))
        {
			return nodes.get(node.getName());
		}
		return null;
	}

	public final List<Directory> getDirectories()
    {
		this.separateNodes();

		Collections.sort(directories);

		return directories;	
	}
	
	public final List<File> getFiles()
    {
		this.separateNodes();
		
		Collections.sort(files);
		
		return files;
	}

    private void separateNodes()
    {
        if(changed)
        {
            Set<String> keys = nodes.keySet();
            Iterator<String> keysIterator = keys.iterator();
            directories = new LinkedList<Directory>();
            files = new LinkedList<File>();
            while(keysIterator.hasNext())
            {
                String key = keysIterator.next();
                Node node = nodes.get(key);
                switch(node.getType())
                {
                    case DIR:
                        directories.add((Directory)node);
                        break;
                    case FILE:
                        files.add((File)node);
                        break;
                }
            }
        }
        changed = false;
    }

	
	
}
