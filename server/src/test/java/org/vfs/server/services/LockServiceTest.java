package org.vfs.server.services;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.vfs.core.network.protocol.User;
import org.vfs.server.model.Node;
import org.vfs.server.model.NodeTypes;
import org.vfs.server.utils.NodePrinter;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Lipatov Nikita
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-test.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LockServiceTest {
    private LockService lockService;
    private NodeManager nodeManager;
    private NodeService nodeService;
    private NodePrinter nodePrinter;

    @Autowired
    public void setServices(LockService lockService, NodeManager nodeManager, NodeService nodeService, NodePrinter nodePrinter) {
        this.lockService = lockService;
        this.nodeManager = nodeManager;
        this.nodeService = nodeService;
        this.nodePrinter = nodePrinter;
        this.nodeService.initDirs();
    }

    @Test
    public void testAddNode() throws Exception {
        Node home = nodeService.getHome();
        Node servers = new Node("servers", NodeTypes.DIR);
        nodeService.getNodeManager().setParent(servers, home);

        Assert.assertTrue(lockService.addNode(servers));
        Assert.assertFalse(lockService.addNode(servers));
    }

    @Test
    public void testIsLock() throws Exception {
        Node home = nodeService.getHome();
        Node servers  = nodeService.findByName(nodeService.getHome(), "servers");
        Node weblogic = nodeService.getNodeManager().newNode("weblogic", NodeTypes.DIR);
        nodeService.getNodeManager().setParent(weblogic, servers);

        User user1 = new User("121", "r1d1");

        Assert.assertTrue(lockService.lock(user1, servers));
        Assert.assertTrue(lockService.isLocked(servers));
        Assert.assertFalse(lockService.isLocked(home));

        Assert.assertEquals(
                "/\n" +
                        "|__home\n" +
                        "|  |__servers [Locked by r1d1 ]\n" +
                        "|  |  |__weblogic\n",
                nodePrinter.print(nodeService.getRoot()));

        lockService.unlock(user1, servers);
        Assert.assertEquals(
                "/\n" +
                        "|__home\n" +
                        "|  |__servers\n" +
                        "|  |  |__weblogic\n",
                nodePrinter.print(nodeService.getRoot()));

    }

    @Test
    public void testRemoveNode() throws Exception {
        nodePrinter = new NodePrinter(lockService);

        Node node = new Node("/", NodeTypes.DIR);
        Node home = new Node("home", NodeTypes.DIR);
        home.setParent(node);

        Assert.assertFalse(lockService.removeNode(home));
        Assert.assertTrue(lockService.addNode(home));
        Assert.assertTrue(lockService.removeNode(home));
        Assert.assertFalse(lockService.removeNode(home));
    }

    @Test
    public void testLock() throws Exception {
        Node home = nodeService.getHome();

        User user1 = new User("121", "nikita");

        Assert.assertTrue(lockService.lock(user1, home));

        Assert.assertEquals(
                "/\n" +
                        "|__home [Locked by nikita ]\n" +
                        "|  |__servers\n" +
                        "|  |  |__weblogic\n",
                nodePrinter.print(nodeService.getRoot()));

        Assert.assertTrue(lockService.isLocked(home));
        Assert.assertEquals(user1, lockService.getUser(home));

        lockService.unlock(user1, home);
        Assert.assertEquals(
                "/\n" +
                        "|__home\n" +
                        "|  |__servers\n" +
                        "|  |  |__weblogic\n",
                nodePrinter.print(nodeService.getRoot()));
    }

    @Test
    public void testLockMultithreading() throws Exception {
        final Node home = nodeService.getHome();
        final Node servers  = nodeService.findByName(home, "servers");
        final Node weblogic = nodeService.findByName(servers, "weblogic");

        int threads = 100;

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        final HashMap<Integer, User> users = new HashMap<>();

        for(final AtomicInteger aint = new AtomicInteger(1); aint.get() <= threads; aint.incrementAndGet()) {
            users.put(aint.get(), new User(aint.toString(), "nikita" + aint.toString()));
            Runnable thread = new Runnable() {
                public void run() {
                    User user = users.get(aint.get());
                    if(!lockService.isLocked(weblogic)) {
                        lockService.lock(user, weblogic);
                    }
                }
            };
            executor.execute(thread);
        }

        executor.shutdown();

        Assert.assertTrue(lockService.isLocked(weblogic));

        // could be any user
        Assert.assertTrue(lockService.getUser(weblogic).getLogin().startsWith("nikita"));
        Assert.assertTrue(
                nodePrinter.print(nodeService.getRoot()).startsWith(
                        "/\n" +
                                "|__home\n" +
                                "|  |__servers\n" +
                                "|  |  |__weblogic [Locked by nikita"
                )
        );

        for(int i = 1; i <= threads; i++) {
            User user = users.get(i);
            lockService.unlockAll(user);
        }

    }

    @Test
    public void testUnlock() throws Exception {
        final Node home = nodeService.getHome();

        User user1 = new User("121", "nikita");
        User user2 = new User("122", "admin");

        Assert.assertTrue(lockService.lock(user1, home));
        Assert.assertTrue(lockService.isLocked(home));

        Assert.assertEquals(
                "/\n" +
                        "|__home [Locked by nikita ]\n" +
                        "|  |__servers\n" +
                        "|  |  |__weblogic\n",
                nodePrinter.print(nodeService.getRoot()));

        Assert.assertEquals(user1, lockService.getUser(home));

        Assert.assertFalse(lockService.unlock(user2, home));
        Assert.assertTrue(lockService.unlock(user1, home));

        Assert.assertEquals(
                "/\n" +
                        "|__home\n" +
                        "|  |__servers\n" +
                        "|  |  |__weblogic\n",
                nodePrinter.print(nodeService.getRoot()));
    }


}
