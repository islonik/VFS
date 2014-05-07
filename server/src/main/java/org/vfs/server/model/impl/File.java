package org.vfs.server.model.impl;

import org.vfs.server.model.Node;

/**
 * @author Lipatov Nikita
 */
public class File extends AbstractNode implements Node
{

	{
		this.type = Node.NodeType.FILE;
	}

	public File (String name)
    {
        super(name);
	}

	public File(Directory parent, String name)
    {
		super(parent, name);
	}

    public File(Node node)
    {
        super(node);
    }

    public Node copy()
    {
        return new File(this);
    }

}
