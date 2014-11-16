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
import org.vfs.server.commands.Command;
import org.vfs.server.model.Timer;
import org.vfs.server.model.UserSession;
import org.vfs.server.network.ClientWriter;
import org.vfs.server.services.NodeService;
import org.vfs.server.services.UserService;
import org.vfs.server.utils.NodePrinter;

import java.net.Socket;
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
    private UserService userService;
    @Autowired
    private NodePrinter nodePrinter;

    private UserSession nikitaSession;
    private UserSession r2d2Session;

    @Before
    public void setUp() throws InterruptedException {
        nodeService.initDirs();

        // UserSession #1
        Socket socket1 = mock(Socket.class);
        Timer timer1 = mock(Timer.class);
        ClientWriter clientWriter1 = mock(ClientWriter.class);
        UserSession userSession1 = userService.startSession(socket1, timer1, clientWriter1);
        String id1 = userSession1.getUser().getId();
        String login1 = "nikita";
        userService.attachUser(id1, login1);
        nikitaSession = userSession1;

        // UserSession #2
        Socket socket2 = mock(Socket.class);
        Timer timer2 = mock(Timer.class);
        ClientWriter clientWriter2 = mock(ClientWriter.class);
        UserSession userSession2 = userService.startSession(socket2, timer2, clientWriter2);
        String id2 = userSession2.getUser().getId();
        String login2 = "r2d2";
        userService.attachUser(id2, login2);
        r2d2Session = userSession2;
    }

    @Test
    public void testChangeDirectory() throws Exception {
        String id = nikitaSession.getUser().getId();
        String login = nikitaSession.getUser().getLogin();
        CommandLine cmd = new CommandLine(commands, nikitaSession);

        cmd.onUserInput(RequestFactory.newRequest(id, login, "cd ../.."));
        cmd.onUserInput(RequestFactory.newRequest(id, login, "cd ../.."));

        Assert.assertEquals(
                "/\n" +
                        "|__home\n" +
                        "|  |__nikita\n" +
                        "|  |__r2d2\n",
                nodePrinter.print(nodeService.getRoot())
        );
    }

    @Test
    public void testCopy() throws Exception {
        String id = nikitaSession.getUser().getId();
        String login = nikitaSession.getUser().getLogin();
        CommandLine cmd = new CommandLine(commands, nikitaSession);

        cmd.onUserInput(RequestFactory.newRequest(id, login, "cd ../.."));
        cmd.onUserInput(RequestFactory.newRequest(id, login, "mkdir applications/servers/weblogic"));
        cmd.onUserInput(RequestFactory.newRequest(id, login, "mkdir logs"));
        cmd.onUserInput(RequestFactory.newRequest(id, login, "copy applications logs"));

        Assert.assertEquals(
                "/\n" +
                        "|__applications\n" +
                        "|  |__servers\n" +
                        "|  |  |__weblogic\n" +
                        "|__home\n" +
                        "|  |__nikita\n" +
                        "|  |__r2d2\n" +
                        "|__logs\n" +
                        "|  |__applications\n" +
                        "|  |  |__servers\n" +
                        "|  |  |  |__weblogic\n",
                nodePrinter.print(nodeService.getRoot())
        );
    }

    @Test
    public void testMove() throws Exception {
        String id = nikitaSession.getUser().getId();
        String login = nikitaSession.getUser().getLogin();
        CommandLine cmd = new CommandLine(commands, nikitaSession);

        cmd.onUserInput(RequestFactory.newRequest(id, login, "cd ../.."));
        cmd.onUserInput(RequestFactory.newRequest(id, login, "mkdir applications/servers/weblogic"));
        cmd.onUserInput(RequestFactory.newRequest(id, login, "mkdir logs"));
        cmd.onUserInput(RequestFactory.newRequest(id, login, "move applications logs"));

        Assert.assertEquals(
                "/\n" +
                        "|__home\n" +
                        "|  |__nikita\n" +
                        "|  |__r2d2\n" +
                        "|__logs\n" +
                        "|  |__applications\n" +
                        "|  |  |__servers\n" +
                        "|  |  |  |__weblogic\n",
                nodePrinter.print(nodeService.getRoot())
        );
    }

    @Test
    public void testRm() throws Exception {
        String id = nikitaSession.getUser().getId();
        String login = nikitaSession.getUser().getLogin();
        CommandLine cmd = new CommandLine(commands, nikitaSession);

        cmd.onUserInput(RequestFactory.newRequest(id, login, "cd ../.."));
        cmd.onUserInput(RequestFactory.newRequest(id, login, "mkdir applications/servers"));
        cmd.onUserInput(RequestFactory.newRequest(id, login, "mkdir logs"));
        cmd.onUserInput(RequestFactory.newRequest(id, login, "rm applications"));

        Assert.assertEquals(
                "/\n" +
                        "|__home\n" +
                        "|  |__nikita\n" +
                        "|  |__r2d2\n" +
                        "|__logs\n",
                nodePrinter.print(nodeService.getRoot())
        );
    }

    @Test
    public void testLockScenario() throws Exception {
        String id1 = nikitaSession.getUser().getId();
        String login1 = nikitaSession.getUser().getLogin();
        String id2 = r2d2Session.getUser().getId();
        String login2 = r2d2Session.getUser().getLogin();
        CommandLine cmd1 = new CommandLine(commands, nikitaSession);
        CommandLine cmd2 = new CommandLine(commands, r2d2Session);

        cmd1.onUserInput(RequestFactory.newRequest(id1, login1, "cd ../.."));
        cmd2.onUserInput(RequestFactory.newRequest(id2, login2, "cd ../.."));

        cmd1.onUserInput(RequestFactory.newRequest(id1, login1, "mkfile applications/servers/weblogic/logs/weblogic.log"));
        cmd2.onUserInput(RequestFactory.newRequest(id2, login2, "mkfile applications/databases/oracle/bin/oracle.exe"));

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
                        "|__home\n" +
                        "|  |__nikita\n" +
                        "|  |__r2d2\n",
                nodePrinter.print(nodeService.getRoot())
        );

        cmd1.onUserInput(RequestFactory.newRequest(id1, login1, "lock applications/databases"));

        Assert.assertEquals(
                "/\n" +
                        "|__applications\n" +
                        "|  |__databases [Locked by nikita ]\n" +
                        "|  |  |__oracle\n" +
                        "|  |  |  |__bin\n" +
                        "|  |  |  |  |__oracle.exe\n" +
                        "|  |__servers\n" +
                        "|  |  |__weblogic\n" +
                        "|  |  |  |__logs\n" +
                        "|  |  |  |  |__weblogic.log\n" +
                        "|__home\n" +
                        "|  |__nikita\n" +
                        "|  |__r2d2\n",
                nodePrinter.print(nodeService.getRoot())
        );

        cmd2.onUserInput(RequestFactory.newRequest(id2, login2, "lock applications"));

        Assert.assertEquals(
                "/\n" +
                        "|__applications [Locked by r2d2 ]\n" +
                        "|  |__databases [Locked by nikita ]\n" +
                        "|  |  |__oracle\n" +
                        "|  |  |  |__bin\n" +
                        "|  |  |  |  |__oracle.exe\n" +
                        "|  |__servers\n" +
                        "|  |  |__weblogic\n" +
                        "|  |  |  |__logs\n" +
                        "|  |  |  |  |__weblogic.log\n" +
                        "|__home\n" +
                        "|  |__nikita\n" +
                        "|  |__r2d2\n",
                nodePrinter.print(nodeService.getRoot())
        );
    }

    @Test
    public void testRecursiveLockScenario() throws Exception {
        String id1 = nikitaSession.getUser().getId();
        String login1 = nikitaSession.getUser().getLogin();
        String id2 = r2d2Session.getUser().getId();
        String login2 = r2d2Session.getUser().getLogin();
        CommandLine cmd1 = new CommandLine(commands, nikitaSession);
        CommandLine cmd2 = new CommandLine(commands, r2d2Session);

        cmd1.onUserInput(RequestFactory.newRequest(id1, login1, "cd ../.."));
        cmd2.onUserInput(RequestFactory.newRequest(id2, login2, "cd ../.."));

        cmd1.onUserInput(RequestFactory.newRequest(id1, login1, "mkfile applications/servers/weblogic/logs/weblogic.log"));
        cmd2.onUserInput(RequestFactory.newRequest(id2, login2, "mkfile applications/databases/oracle/bin/oracle.exe"));

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
                        "|__home\n" +
                        "|  |__nikita\n" +
                        "|  |__r2d2\n",
                nodePrinter.print(nodeService.getRoot())
        );

        cmd1.onUserInput(RequestFactory.newRequest(id1, login1, "lock -r applications/databases"));

        Assert.assertEquals(
                "/\n" +
                        "|__applications\n" +
                        "|  |__databases [Locked by nikita ]\n" +
                        "|  |  |__oracle [Locked by nikita ]\n" +
                        "|  |  |  |__bin [Locked by nikita ]\n" +
                        "|  |  |  |  |__oracle.exe [Locked by nikita ]\n" +
                        "|  |__servers\n" +
                        "|  |  |__weblogic\n" +
                        "|  |  |  |__logs\n" +
                        "|  |  |  |  |__weblogic.log\n" +
                        "|__home\n" +
                        "|  |__nikita\n" +
                        "|  |__r2d2\n",
                nodePrinter.print(nodeService.getRoot())
        );

        cmd2.onUserInput(RequestFactory.newRequest(id2, login2, "lock -r applications"));

        Assert.assertEquals(
                "/\n" +
                        "|__applications\n" +
                        "|  |__databases [Locked by nikita ]\n" +
                        "|  |  |__oracle [Locked by nikita ]\n" +
                        "|  |  |  |__bin [Locked by nikita ]\n" +
                        "|  |  |  |  |__oracle.exe [Locked by nikita ]\n" +
                        "|  |__servers\n" +
                        "|  |  |__weblogic\n" +
                        "|  |  |  |__logs\n" +
                        "|  |  |  |  |__weblogic.log\n" +
                        "|__home\n" +
                        "|  |__nikita\n" +
                        "|  |__r2d2\n",
                nodePrinter.print(nodeService.getRoot())
        );
    }

    @Test
    public void testRename() throws Exception {
        String id1 = nikitaSession.getUser().getId();
        String login1 = nikitaSession.getUser().getLogin();
        String id2 = r2d2Session.getUser().getId();
        String login2 = r2d2Session.getUser().getLogin();
        CommandLine cmd1 = new CommandLine(commands, nikitaSession);
        CommandLine cmd2 = new CommandLine(commands, r2d2Session);

        cmd1.onUserInput(RequestFactory.newRequest(id1, login1, "cd ../.."));
        cmd2.onUserInput(RequestFactory.newRequest(id2, login2, "cd ../.."));

        cmd1.onUserInput(RequestFactory.newRequest(id1, login1, "mkfile applications/servers/weblogic/logs/weblogic.log"));
        cmd1.onUserInput(RequestFactory.newRequest(id1, login1, "lock -r applications/servers/weblogic"));

        cmd2.onUserInput(RequestFactory.newRequest(id2, login2, "rename applications/servers web-servers"));

        Assert.assertEquals(
                "/\n" +
                        "|__applications\n" +
                        "|  |__web-servers\n" +
                        "|  |  |__weblogic [Locked by nikita ]\n" +
                        "|  |  |  |__logs [Locked by nikita ]\n" +
                        "|  |  |  |  |__weblogic.log [Locked by nikita ]\n" +
                        "|__home\n" +
                        "|  |__nikita\n" +
                        "|  |__r2d2\n",
                nodePrinter.print(nodeService.getRoot())
        );

    }

}
