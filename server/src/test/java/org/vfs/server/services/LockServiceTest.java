package org.vfs.server.services;

import org.junit.Assert;
import org.junit.Test;
import org.vfs.core.network.protocol.User;
import org.vfs.server.model.Node;
import org.vfs.server.model.NodeTypes;

/**
 * @author Lipatov Nikita
 */
public class LockServiceTest {
    private LockService lockService;
    private NodeService nodeService;

    @Test
    public void testAddNode() throws Exception {
        lockService = new LockService();

        Node node = new Node("/", NodeTypes.DIR);
        Node home = new Node("home", NodeTypes.DIR);
        home.setParent(node);

        Assert.assertTrue(lockService.addNode(home));
        Assert.assertFalse(lockService.addNode(home));
    }

    @Test
    public void testRemoveNode() throws Exception {
        lockService = new LockService();

        Node node = new Node("/", NodeTypes.DIR);
        Node home = new Node("home", NodeTypes.DIR);
        home.setParent(node);

        Assert.assertFalse(lockService.removeNode(home));
        Assert.assertTrue(lockService.addNode(home));
        Assert.assertTrue(lockService.removeNode(home));
        Assert.assertFalse(lockService.removeNode(home));
    }

    @Test
    public void testIsLock() throws Exception {
        lockService = new LockService();
        nodeService = new NodeService("/", lockService);

        Node home = nodeService.getHome();
        Node servers = nodeService.newNode("servers", NodeTypes.DIR);
        nodeService.setParent(servers, home);
        Node weblogic = nodeService.newNode("weblogic", NodeTypes.DIR);
        nodeService.setParent(weblogic, servers);

        User user1 = new User("121", "r1d1");

        Assert.assertTrue(lockService.lock(user1, servers));
        Assert.assertTrue(lockService.isLocked(servers));
        Assert.assertFalse(lockService.isLocked(home));

        Assert.assertEquals(
                "/\n" +
                        "|__home\n" +
                        "|  |__servers [Locked by r1d1 ]\n" +
                        "|  |  |__weblogic\n",
                nodeService.printTree(nodeService.getRoot()));
    }

    @Test
    public void testLock() throws Exception {
        lockService = new LockService();
        nodeService = new NodeService("/", lockService);

        Node home = nodeService.getHome();
        Node servers = nodeService.newNode("servers", NodeTypes.DIR);
        nodeService.setParent(servers, home);
        Node weblogic = nodeService.newNode("weblogic", NodeTypes.DIR);
        nodeService.setParent(weblogic, servers);

        User user1 = new User("121", "nikita");

        Assert.assertTrue(lockService.lock(user1, home));

        Assert.assertEquals(
                "/\n" +
                        "|__home [Locked by nikita ]\n" +
                        "|  |__servers\n" +
                        "|  |  |__weblogic\n",
                nodeService.printTree(nodeService.getRoot()));

        Assert.assertTrue(lockService.isLocked(home));
        Assert.assertEquals(user1, lockService.getUser(home));
    }


    @Test
    public void testUnlock() throws Exception {
        lockService = new LockService();
        nodeService = new NodeService("/", lockService);

        Node home = nodeService.getHome();
        Node servers = nodeService.newNode("servers", NodeTypes.DIR);
        nodeService.setParent(servers, home);
        Node weblogic = nodeService.newNode("weblogic", NodeTypes.DIR);
        nodeService.setParent(weblogic, servers);

        User user1 = new User("121", "nikita");
        User user2 = new User("122", "admin");

        Assert.assertTrue(lockService.lock(user1, home));
        Assert.assertTrue(lockService.isLocked(home));

        Assert.assertEquals(
                "/\n" +
                        "|__home [Locked by nikita ]\n" +
                        "|  |__servers\n" +
                        "|  |  |__weblogic\n",
                nodeService.printTree(nodeService.getRoot()));

        Assert.assertEquals(user1, lockService.getUser(home));

        Assert.assertFalse(lockService.unlock(user2, home));
        Assert.assertTrue(lockService.unlock(user1, home));

        Assert.assertEquals(
                "/\n" +
                        "|__home\n" +
                        "|  |__servers\n" +
                        "|  |  |__weblogic\n",
                nodeService.printTree(nodeService.getRoot()));
    }
}
