package org.vfs.server.model;

import org.vfs.server.model.impl.Directory;

/**
 * @author Lipatov Nikita
 */
public class Tree
{
    private static Tree instance;
    private Directory root;

    public Tree()
    {
        root = new Directory("/");
    }

    /**
     *  Double checked locking (since java 1.5+)
     **/
    public static Tree getInstance()
    {
        Tree localInstance = instance;
        if(localInstance == null)
        {
            synchronized (Tree.class)
            {
                localInstance = instance;
                if(localInstance == null)
                {
                    instance = localInstance = new Tree();
                }
            }
        }
        return localInstance;
    }

    public static void cleanup()
    {
        instance = null;
    }

    public Directory getRoot()
    {
        return root;
    }

    public void setRoot(Directory root)
    {
        this.root = root;
    }

}
