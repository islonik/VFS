package org.vfs.server.model.impl;

import org.vfs.core.network.protocol.User;
import org.vfs.server.command.CommandLine;
import org.vfs.server.model.Node;
import org.vfs.server.model.Tree;

/**
 * @author Lipatov Nikita
 */
public abstract class AbstractNode implements Comparable<Node>
{
	protected Node.NodeType type = null;
	protected String name = null;
    protected Directory parent = null;
    protected boolean isLock = false;
    protected User lockByUser;

    public AbstractNode(String name)
    {
        this.name = name;
    }

    public AbstractNode(Directory parent, String name)
    {
        this.parent = parent;
        this.name = name;
    }

    public AbstractNode(Node node)
    {
        this.type = node.getType();
        this.name = node.getName();
        this.parent = node.getParent();
        this.isLock = node.isLock();
        this.lockByUser = node.getLockByUser();
    }

	public void setName(String name)
    {
		this.name = name;
	}

	public String getName()
    {
		return name;
	}

	public Node.NodeType getType()
    {
		return type;
	}

    public Directory getParent()
    {
        return parent;
    }

    public void setParent(Directory parent)
    {
        this.parent = parent;
    }

    public boolean isLock()
    {
        return isLock;
    }

    public boolean isLock(User user)
    {
        User localUser = this.getLockByUser();
        if(localUser != null && localUser.equals(user))
        {
            return isLock;
        }
        return false;
    }

    public void setLock(User user, boolean isLock)
    {
        if(this.isLock) // lock already exist
        {
            if(this.lockByUser.equals(user))
            {
                this.lockByUser = (isLock) ? user : null;
                this.isLock = isLock;
            }
        }
        else // no lock
        {
            this.lockByUser = (isLock) ? user : null;
            this.isLock = isLock;
        }
    }

    public User getLockByUser()
    {
        return lockByUser;
    }

	public int compareTo(Node node)
    {
		return name.compareTo(node.getName());
	}

    public String getFullPath()
    {
        String path = null;
        String result = name;
        if(parent != null)
        {
            path = parent.getFullPath();
        }
        if(path != null)
        {
            result = path + "/" + result;
        }
        return removeDoubleSlashes(Tree.getInstance().getRoot().getName() + result);
    }

    private String removeDoubleSlashes(String path)
    {
        if(path.contains("//"))
        {
            path = path.replaceAll("//", "/");
            return removeDoubleSlashes(path);
        }
        return path;
    }

}
