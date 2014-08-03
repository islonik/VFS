package org.vfs.server;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.vfs.core.network.protocol.RequestFactory;
import org.vfs.core.network.protocol.User;
import org.vfs.server.model.Node;
import org.vfs.server.model.UserSession;
import org.vfs.server.network.ClientWriter;
import org.vfs.server.services.LockService;
import org.vfs.server.services.NodeService;
import org.vfs.server.services.UserService;
import org.vfs.server.utils.NodePrinter;

import static org.mockito.Mockito.*;

/**
 * @author: Lipatov Nikita
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CommandLineTest {

    private LockService lockService;
    private NodeService nodeService;
    private NodePrinter nodePrinter;

    private Node root;

    @Test
    public void testCopy() throws Exception {
        lockService = new LockService();
        nodePrinter = new NodePrinter(lockService);
        nodeService = new NodeService("/", lockService);
        
        root = nodeService.getRoot();
        
        UserService userService = new UserService(nodeService);
        UserSession userSession1 = new UserSession();

        User user1 = new User();
        user1.setId("11");
        user1.setLogin("r1d1");

        userSession1.setUser(user1);
        userSession1.setNode(root);

        ClientWriter clientWriter = mock(ClientWriter.class);

        CommandLine cmd = new CommandLine(lockService, nodeService, userService, userSession1, clientWriter);

        cmd.onUserInput(RequestFactory.newRequest("12", "nikita", "mkdir applications/servers/weblogic"));
        cmd.onUserInput(RequestFactory.newRequest("12", "nikita", "mkdir logs"));
        cmd.onUserInput(RequestFactory.newRequest("12", "nikita", "copy applications logs"));

        Assert.assertEquals(
                "/\n" +
                        "|__applications\n" +
                        "|  |__servers\n" +
                        "|  |  |__weblogic\n" +
                        "|__home\n" +
                        "|__logs\n" +
                        "|  |__applications\n" +
                        "|  |  |__servers\n" +
                        "|  |  |  |__weblogic\n",
                nodePrinter.print(root)
        );
    }

    @Test
    public void testMove() throws Exception {
        lockService = new LockService();
        nodePrinter = new NodePrinter(lockService);
        nodeService = new NodeService("/", lockService);
        root = nodeService.getRoot();

        UserService userService = new UserService(nodeService);
        UserSession userSession1 = new UserSession();

        User user1 = new User();
        user1.setId("11");
        user1.setLogin("r1d1");

        userSession1.setUser(user1);
        userSession1.setNode(root);

        ClientWriter clientWriter = mock(ClientWriter.class);

        CommandLine cmd = new CommandLine(lockService, nodeService, userService, userSession1, clientWriter);

        cmd.onUserInput(RequestFactory.newRequest("12", "nikita", "mkdir applications/servers/weblogic"));
        cmd.onUserInput(RequestFactory.newRequest("12", "nikita", "mkdir logs"));
        cmd.onUserInput(RequestFactory.newRequest("12", "nikita", "move applications logs"));

        Assert.assertEquals(
                "/\n" +
                        "|__home\n" +
                        "|__logs\n" +
                        "|  |__applications\n" +
                        "|  |  |__servers\n" +
                        "|  |  |  |__weblogic\n",
                nodePrinter.print(root)
        );
    }

    @Test
    public void testRm() throws Exception {
        lockService = new LockService();
        nodePrinter = new NodePrinter(lockService);
        nodeService = new NodeService("/", lockService);
        root = nodeService.getRoot();

        UserService userService = new UserService(nodeService);
        UserSession userSession1 = new UserSession();

        User user1 = new User();
        user1.setId("11");
        user1.setLogin("r1d1");

        userSession1.setUser(user1);
        userSession1.setNode(root);

        ClientWriter clientWriter = mock(ClientWriter.class);

        CommandLine cmd = new CommandLine(lockService, nodeService, userService, userSession1, clientWriter);

        cmd.onUserInput(RequestFactory.newRequest("12", "nikita", "mkdir applications/servers"));
        cmd.onUserInput(RequestFactory.newRequest("12", "nikita", "mkdir logs"));
        cmd.onUserInput(RequestFactory.newRequest("12", "nikita", "rm applications"));

        Assert.assertEquals(
                "/\n" +
                        "|__home\n" +
                        "|__logs\n",
                nodePrinter.print(root)
        );
    }

    @Test
    public void testLockScenario() throws Exception {
        lockService = new LockService();
        nodePrinter = new NodePrinter(lockService);
        nodeService = new NodeService("/", lockService);
        root = nodeService.getRoot();

        UserService userService = new UserService(nodeService);
        UserSession userSession1 = new UserSession();
        UserSession userSession2 = new UserSession();

        User user1 = new User();
        user1.setId("11");
        user1.setLogin("r1d1");

        User user2 = new User();
        user2.setId("22");
        user2.setLogin("r2d2");

        userSession1.setUser(user1);
        userSession1.setNode(root);

        userSession2.setUser(user2);
        userSession2.setNode(root);

        ClientWriter clientWriter = mock(ClientWriter.class);

        CommandLine cmd1 = new CommandLine(lockService, nodeService, userService, userSession1, clientWriter);

        CommandLine cmd2 = new CommandLine(lockService, nodeService, userService, userSession2, clientWriter);

        cmd1.onUserInput(RequestFactory.newRequest("11", "r1d1", "mkfile applications/servers/weblogic/logs/weblogic.log"));

        cmd2.onUserInput(RequestFactory.newRequest("22", "r2d2", "mkfile applications/databases/oracle/bin/oracle.exe"));

        Assert.assertEquals(
                "/\n" +
                        "|__applications\n" +
                        "|  |__databases\n" +
                        "|  |  |__oracle\n" +
                        "|  |  |  |__bin\n" +
                        "|  |  |  |  |__oracle.exe\n" +
                        "|  |__servers\n" +
                        "|  |  |__weblogic\n" +
                        "|  |  |  |__logs\n" +
                        "|  |  |  |  |__weblogic.log\n" +
                        "|__home\n",
                nodePrinter.print(root)
        );

        cmd1.onUserInput(RequestFactory.newRequest("11", "r1d1", "lock applications/databases"));

        Assert.assertEquals(
                "/\n" +
                        "|__applications\n" +
                        "|  |__databases [Locked by r1d1 ]\n" +
                        "|  |  |__oracle\n" +
                        "|  |  |  |__bin\n" +
                        "|  |  |  |  |__oracle.exe\n" +
                        "|  |__servers\n" +
                        "|  |  |__weblogic\n" +
                        "|  |  |  |__logs\n" +
                        "|  |  |  |  |__weblogic.log\n" +
                        "|__home\n",
                nodePrinter.print(root)
        );

        cmd2.onUserInput(RequestFactory.newRequest("22", "r2d2", "lock applications"));

        Assert.assertEquals(
                "/\n" +
                        "|__applications [Locked by r2d2 ]\n" +
                        "|  |__databases [Locked by r1d1 ]\n" +
                        "|  |  |__oracle\n" +
                        "|  |  |  |__bin\n" +
                        "|  |  |  |  |__oracle.exe\n" +
                        "|  |__servers\n" +
                        "|  |  |__weblogic\n" +
                        "|  |  |  |__logs\n" +
                        "|  |  |  |  |__weblogic.log\n" +
                        "|__home\n",
                nodePrinter.print(root)
        );
    }

    @Test
    public void testRecursiveLockScenario() throws Exception {
        lockService = new LockService();
        nodePrinter = new NodePrinter(lockService);
        nodeService = new NodeService("/", lockService);
        root = nodeService.getRoot();

        UserService userService = new UserService(nodeService);
        UserSession userSession1 = new UserSession();
        UserSession userSession2 = new UserSession();

        User user1 = new User();
        user1.setId("11");
        user1.setLogin("r1d1");

        User user2 = new User();
        user2.setId("22");
        user2.setLogin("r2d2");

        userSession1.setUser(user1);
        userSession1.setNode(root);

        userSession2.setUser(user2);
        userSession2.setNode(root);

        ClientWriter clientWriter = mock(ClientWriter.class);

        CommandLine cmd1 = new CommandLine(lockService, nodeService, userService, userSession1, clientWriter);

        CommandLine cmd2 = new CommandLine(lockService, nodeService, userService, userSession2, clientWriter);

        cmd1.onUserInput(RequestFactory.newRequest("11", "r1d1", "mkfile applications/servers/weblogic/logs/weblogic.log"));

        cmd2.onUserInput(RequestFactory.newRequest("22", "r2d2", "mkfile applications/databases/oracle/bin/oracle.exe"));

        Assert.assertEquals(
                "/\n" +
                        "|__applications\n" +
                        "|  |__databases\n" +
                        "|  |  |__oracle\n" +
                        "|  |  |  |__bin\n" +
                        "|  |  |  |  |__oracle.exe\n" +
                        "|  |__servers\n" +
                        "|  |  |__weblogic\n" +
                        "|  |  |  |__logs\n" +
                        "|  |  |  |  |__weblogic.log\n" +
                        "|__home\n",
                nodePrinter.print(root)
        );

        cmd1.onUserInput(RequestFactory.newRequest("11", "r1d1", "lock -r applications/databases"));

        Assert.assertEquals(
                "/\n" +
                        "|__applications\n" +
                        "|  |__databases [Locked by r1d1 ]\n" +
                        "|  |  |__oracle [Locked by r1d1 ]\n" +
                        "|  |  |  |__bin [Locked by r1d1 ]\n" +
                        "|  |  |  |  |__oracle.exe [Locked by r1d1 ]\n" +
                        "|  |__servers\n" +
                        "|  |  |__weblogic\n" +
                        "|  |  |  |__logs\n" +
                        "|  |  |  |  |__weblogic.log\n" +
                        "|__home\n",
                nodePrinter.print(root)
        );

        cmd2.onUserInput(RequestFactory.newRequest("22", "r2d2", "lock -r applications"));

        Assert.assertEquals(
                "/\n" +
                        "|__applications\n" +
                        "|  |__databases [Locked by r1d1 ]\n" +
                        "|  |  |__oracle [Locked by r1d1 ]\n" +
                        "|  |  |  |__bin [Locked by r1d1 ]\n" +
                        "|  |  |  |  |__oracle.exe [Locked by r1d1 ]\n" +
                        "|  |__servers\n" +
                        "|  |  |__weblogic\n" +
                        "|  |  |  |__logs\n" +
                        "|  |  |  |  |__weblogic.log\n" +
                        "|__home\n",
                nodePrinter.print(root)
        );
    }

    @Test
    public void testRename() throws Exception {
        lockService = new LockService();
        nodePrinter = new NodePrinter(lockService);
        nodeService = new NodeService("/", lockService);
        root = nodeService.getRoot();

        UserService userService = new UserService(nodeService);
        UserSession userSession1 = new UserSession();
        UserSession userSession2 = new UserSession();

        User user1 = new User();
        user1.setId("11");
        user1.setLogin("r1d1");

        User user2 = new User();
        user2.setId("22");
        user2.setLogin("r2d2");

        userSession1.setUser(user1);
        userSession1.setNode(root);

        userSession2.setUser(user2);
        userSession2.setNode(root);

        ClientWriter clientWriter = mock(ClientWriter.class);

        CommandLine cmd1 = new CommandLine(lockService, nodeService, userService, userSession1, clientWriter);

        CommandLine cmd2 = new CommandLine(lockService, nodeService, userService, userSession2, clientWriter);

        cmd1.onUserInput(RequestFactory.newRequest("11", "r1d1", "mkfile applications/servers/weblogic/logs/weblogic.log"));
        cmd1.onUserInput(RequestFactory.newRequest("11", "r1d1", "lock -r applications/servers/weblogic"));

        cmd2.onUserInput(RequestFactory.newRequest("22", "r2d2", "rename applications/servers web-servers"));

        Assert.assertEquals(
                "/\n" +
                        "|__applications\n" +
                        "|  |__web-servers\n" +
                        "|  |  |__weblogic [Locked by r1d1 ]\n" +
                        "|  |  |  |__logs [Locked by r1d1 ]\n" +
                        "|  |  |  |  |__weblogic.log [Locked by r1d1 ]\n" +
                        "|__home\n",
                nodePrinter.print(root)
        );

    }

}
