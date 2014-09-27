package org.vfs.server;

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
import org.vfs.core.network.protocol.RequestFactory;
import org.vfs.core.network.protocol.User;
import org.vfs.server.commands.Command;
import org.vfs.server.model.UserSession;
import org.vfs.server.network.ClientWriter;
import org.vfs.server.services.NodeService;
import org.vfs.server.utils.NodePrinter;

import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * All common tests, except LockTests.
 * @author: Lipatov Nikita
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-test.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CommandLineTest {

    @Autowired
    private Map<String, Command> commands;
    @Autowired
    private NodeService nodeService;
    @Autowired
    private NodePrinter nodePrinter;

    @Before
    public void setUp() throws InterruptedException {
        nodeService.initDirs();
    }

    @Test
    public void testCopy() throws Exception {
        UserSession userSession1 = new UserSession();

        User user1 = new User();
        user1.setId("11");
        user1.setLogin("r1d1");

        userSession1.setUser(user1);
        userSession1.setNode(nodeService.getRoot());

        ClientWriter clientWriter = mock(ClientWriter.class);
        userSession1.setClientWriter(clientWriter);

        CommandLine cmd = new CommandLine(commands, userSession1);

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
                nodePrinter.print(nodeService.getRoot())
        );
    }

    @Test
    public void testMove() throws Exception {
        UserSession userSession1 = new UserSession();

        User user1 = new User();
        user1.setId("11");
        user1.setLogin("r1d1");

        userSession1.setUser(user1);
        userSession1.setNode(nodeService.getRoot());

        ClientWriter clientWriter = mock(ClientWriter.class);
        userSession1.setClientWriter(clientWriter);

        CommandLine cmd = new CommandLine(commands, userSession1);

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
                nodePrinter.print(nodeService.getRoot())
        );
    }

    @Test
    public void testRm() throws Exception {
        UserSession userSession1 = new UserSession();

        User user1 = new User();
        user1.setId("11");
        user1.setLogin("r1d1");

        userSession1.setUser(user1);
        userSession1.setNode(nodeService.getRoot());

        ClientWriter clientWriter = mock(ClientWriter.class);
        userSession1.setClientWriter(clientWriter);

        CommandLine cmd = new CommandLine(commands, userSession1);

        cmd.onUserInput(RequestFactory.newRequest("12", "nikita", "mkdir applications/servers"));
        cmd.onUserInput(RequestFactory.newRequest("12", "nikita", "mkdir logs"));
        cmd.onUserInput(RequestFactory.newRequest("12", "nikita", "rm applications"));

        Assert.assertEquals(
                "/\n" +
                        "|__home\n" +
                        "|__logs\n",
                nodePrinter.print(nodeService.getRoot())
        );
    }

    @Test
    public void testLockScenario() throws Exception {
        UserSession userSession1 = new UserSession();
        UserSession userSession2 = new UserSession();

        User user1 = new User();
        user1.setId("11");
        user1.setLogin("r1d1");

        User user2 = new User();
        user2.setId("22");
        user2.setLogin("r2d2");

        userSession1.setUser(user1);
        userSession1.setNode(nodeService.getRoot());

        userSession2.setUser(user2);
        userSession2.setNode(nodeService.getRoot());

        ClientWriter clientWriter = mock(ClientWriter.class);
        userSession1.setClientWriter(clientWriter);
        userSession2.setClientWriter(clientWriter);

        CommandLine cmd1 = new CommandLine(commands, userSession1);

        CommandLine cmd2 = new CommandLine(commands, userSession2);

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
                nodePrinter.print(nodeService.getRoot())
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
                nodePrinter.print(nodeService.getRoot())
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
                nodePrinter.print(nodeService.getRoot())
        );
    }

    @Test
    public void testRecursiveLockScenario() throws Exception {
        UserSession userSession1 = new UserSession();
        UserSession userSession2 = new UserSession();

        User user1 = new User();
        user1.setId("11");
        user1.setLogin("r1d1");

        User user2 = new User();
        user2.setId("22");
        user2.setLogin("r2d2");

        userSession1.setUser(user1);
        userSession1.setNode(nodeService.getRoot());

        userSession2.setUser(user2);
        userSession2.setNode(nodeService.getRoot());

        ClientWriter clientWriter = mock(ClientWriter.class);
        userSession1.setClientWriter(clientWriter);
        userSession2.setClientWriter(clientWriter);

        CommandLine cmd1 = new CommandLine(commands, userSession1);

        CommandLine cmd2 = new CommandLine(commands, userSession2);

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
                nodePrinter.print(nodeService.getRoot())
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
                nodePrinter.print(nodeService.getRoot())
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
                nodePrinter.print(nodeService.getRoot())
        );
    }

    @Test
    public void testRename() throws Exception {
        UserSession userSession1 = new UserSession();
        UserSession userSession2 = new UserSession();

        User user1 = new User();
        user1.setId("11");
        user1.setLogin("r1d1");

        User user2 = new User();
        user2.setId("22");
        user2.setLogin("r2d2");

        userSession1.setUser(user1);
        userSession1.setNode(nodeService.getRoot());

        userSession2.setUser(user2);
        userSession2.setNode(nodeService.getRoot());

        ClientWriter clientWriter = mock(ClientWriter.class);
        userSession1.setClientWriter(clientWriter);
        userSession2.setClientWriter(clientWriter);

        CommandLine cmd1 = new CommandLine(commands, userSession1);

        CommandLine cmd2 = new CommandLine(commands, userSession2);

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
                nodePrinter.print(nodeService.getRoot())
        );

    }

}
