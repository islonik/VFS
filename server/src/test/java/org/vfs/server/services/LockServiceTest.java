package org.vfs.server.services;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LockServiceTest {

    @Autowired
    private LockService lockService;
    @Autowired
    private NodeService nodeService;
    @Autowired
    private NodePrinter nodePrinter;

    @Before
    public void setUp() throws InterruptedException {
        nodeService.initDirs();
    }

    @Test
    public void testAddNode() throws Exception {
        Assert.assertTrue("This test is checked the impossibility of double adding into LockService", true);

        Node home = nodeService.getHome();
        Node servers = new Node("servers", NodeTypes.DIR);
        nodeService.getNodeManager().setParent(servers, home);

        Assert.assertTrue(lockService.addNode(servers));
        Assert.assertFalse(lockService.addNode(servers));
    }

    @Test
    public void testIsLock() throws Exception {
        Node home = nodeService.getHome();
        Node servers = nodeService.getNodeManager().newNode("servers", NodeTypes.DIR);
        nodeService.getNodeManager().setParent(servers, home);
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
        Node node = new Node("/", NodeTypes.DIR);
        Node home = new Node("home", NodeTypes.DIR);
        home.setParent(node);

        Assert.assertFalse(lockService.removeNode(home));
        Assert.assertTrue(lockService.addNode(home));
        Assert.assertTrue(lockService.removeNode(home));
        Assert.assertFalse(lockService.removeNode(home));
    }

    @Test
    public void testRemoveNodes() throws Exception {
        Node home = nodeService.getHome();
        Node applications = nodeService.getNodeManager().newNode("applications", NodeTypes.DIR);
        nodeService.getNodeManager().setParent(applications, home);
        Node weblogic     = nodeService.getNodeManager().newNode("weblogic",     NodeTypes.DIR);
        nodeService.getNodeManager().setParent(weblogic, applications);
        Node oracle       = nodeService.getNodeManager().newNode("oracle",       NodeTypes.DIR);
        nodeService.getNodeManager().setParent(oracle, weblogic);
        Node logs = nodeService.getNodeManager().newNode("logs", NodeTypes.DIR);
        nodeService.getNodeManager().setParent(logs, home);
        Node clone = nodeService.clone(applications);
        nodeService.getNodeManager().setParent(clone, logs);
        nodeService.removeNode(home, "applications");

        // 6 node should exist
        Assert.assertEquals(6, lockService.getLockMapSize());
    }

    @Test
    public void testLock() throws Exception {
        Node home = nodeService.getHome();
        Node servers = nodeService.getNodeManager().newNode("servers", NodeTypes.DIR);
        nodeService.getNodeManager().setParent(servers, home);
        Node weblogic = nodeService.getNodeManager().newNode("weblogic", NodeTypes.DIR);
        nodeService.getNodeManager().setParent(weblogic, servers);

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
        final Node servers = nodeService.getNodeManager().newNode("servers", NodeTypes.DIR);
        nodeService.getNodeManager().setParent(servers, home);
        final Node weblogic = nodeService.getNodeManager().newNode("weblogic", NodeTypes.DIR);
        nodeService.getNodeManager().setParent(weblogic, servers);

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
    }

    @Test
    public void testUnlock() throws Exception {
        final Node home = nodeService.getHome();
        Node servers = nodeService.getNodeManager().newNode("servers", NodeTypes.DIR);
        nodeService.getNodeManager().setParent(servers, home);
        Node weblogic = nodeService.getNodeManager().newNode("weblogic", NodeTypes.DIR);
        nodeService.getNodeManager().setParent(weblogic, servers);


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
