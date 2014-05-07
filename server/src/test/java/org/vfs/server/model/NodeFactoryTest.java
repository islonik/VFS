package org.vfs.server.model;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.vfs.server.model.impl.Directory;

/**
 * User: Lipatov Nikita
 */
public class NodeFactoryTest
{

    @BeforeClass
    public static void beforeClass()
    {
        Tree.cleanup();
    }

    @Test
    public void testNodeFactory_testCase01()
    {
        Directory homeDirectory = NodeFactory.getFactory().createDirectory("home/user1");

        Assert.assertEquals("user1", homeDirectory.getName());
        Assert.assertEquals("/home/user1", homeDirectory.getFullPath());
    }

    @Test
    public void testNodeFactory_testCase02()
    {
        Directory homeDirectory = NodeFactory.getFactory().createDirectory("//home/user2");

        Assert.assertEquals("user2", homeDirectory.getName());
        Assert.assertEquals("/home/user2", homeDirectory.getFullPath());
    }
}
