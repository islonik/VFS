package org.vfs.server.model;

import org.junit.Assert;
import org.junit.Test;
import org.vfs.core.network.protocol.User;
import org.vfs.server.model.impl.Directory;
import org.vfs.server.model.impl.File;

/**
 * User: Lipatov Nikita
 */
public class NodeTest
{

    @Test
    public void testNode_add_01()
    {
        Directory node01 = NodeFactory.getFactory().createDirectory("org");
        Directory node02 = NodeFactory.getFactory().createDirectory("vfs");
        Directory node03 = NodeFactory.getFactory().createDirectory("server");

        node01.addNode(node02);
        node02.addNode(node03);

        Assert.assertEquals("/org/vfs/server", node03.getFullPath());
    }

    @Test
    public void testNode_add_02()
    {
        Directory node01 = NodeFactory.getFactory().createDirectory("org");
        Directory node02 = NodeFactory.getFactory().createDirectory("vfs");
        File node03 = NodeFactory.getFactory().createFile("test.jsp");

        node01.addNode(node02);
        node02.addNode(node03);

        Assert.assertEquals("/org/vfs/test.jsp", node03.getFullPath());
    }

    @Test
    public void testNode_lock_01()
    {
        User user1 = new User();
        user1.setLogin("testUser");
        User user2 = new User();
        user2.setLogin("testUser");

        Node node01 = NodeFactory.getFactory().createDirectory("org");

        Assert.assertFalse(node01.isLock());
        node01.setLock(user1, true);
        Assert.assertTrue(node01.isLock());
        node01.setLock(user1, false);
        Assert.assertFalse(node01.isLock());
        node01.setLock(user1, true);
        Assert.assertTrue(node01.isLock());

        node01.setLock(user2, true);
        Assert.assertEquals(user1, node01.getLockByUser());
    }

    @Test
    public void testNode_lock_02()
    {
        User user1 = new User();
        user1.setLogin("testUser");
        User user2 = new User();
        user2.setLogin("testUser");

        Node node01 = NodeFactory.getFactory().createFile("test.jpg");

        Assert.assertFalse(node01.isLock());
        Assert.assertNull(node01.getLockByUser());
        node01.setLock(user1, true);
        Assert.assertTrue(node01.isLock());
        Assert.assertNotNull(node01.getLockByUser());
        node01.setLock(user1, false);
        Assert.assertFalse(node01.isLock());
        Assert.assertNull(node01.getLockByUser());
        node01.setLock(user1, true);
        Assert.assertTrue(node01.isLock());
        Assert.assertNotNull(node01.getLockByUser());

        node01.setLock(user2, true);
        Assert.assertEquals(user1, node01.getLockByUser());
    }
}
