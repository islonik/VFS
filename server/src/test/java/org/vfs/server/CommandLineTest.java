package org.vfs.server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.vfs.core.network.protocol.RequestFactory;
import org.vfs.server.commands.Command;
import org.vfs.server.model.UserSession;
import org.vfs.server.network.ClientWriter;
import org.vfs.server.services.NodeService;
import org.vfs.server.services.UserSessionService;
import org.vfs.server.utils.NodePrinter;

import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * All common tests, except LockTests.
 * @author Lipatov Nikita
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles(value = "test")
@TestMethodOrder(MethodOrderer.MethodName.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CommandLineTest {

    @Autowired
    private Map<String, Command> commands;
    @Autowired
    private NodeService nodeService;
    @Autowired
    private UserSessionService userSessionService;
    @Autowired
    private NodePrinter nodePrinter;

    private UserSession nikitaSession;
    private UserSession r2d2Session;

    @BeforeEach
    public void setUp() {
        nodeService.initDirs();

        // UserSession #1
        ClientWriter clientWriter1 = mock(ClientWriter.class);
        UserSession userSession1 = userSessionService.startSession(clientWriter1);
        String id1 = userSession1.getUser().getId();
        String login1 = "nikita";
        userSessionService.attachUser(id1, login1);
        nikitaSession = userSession1;

        // UserSession #2
        ClientWriter clientWriter2 = mock(ClientWriter.class);
        UserSession userSession2 = userSessionService.startSession(clientWriter2);
        String id2 = userSession2.getUser().getId();
        String login2 = "r2d2";
        userSessionService.attachUser(id2, login2);
        r2d2Session = userSession2;
    }

    @Test
    public void testChangeDirectory() {
        String id = nikitaSession.getUser().getId();
        String login = nikitaSession.getUser().getLogin();
        CommandLine cmd = new CommandLine(commands);

        cmd.onUserInput(nikitaSession, RequestFactory.newRequest(id, login, "cd ../.."));
        cmd.onUserInput(nikitaSession, RequestFactory.newRequest(id, login, "cd ../.."));

        Assertions.assertEquals(
                """
                /
                |__home
                |  |__nikita
                |  |__r2d2
                """,
                nodePrinter.print(nodeService.getRoot())
        );
    }

    @Test
    public void testCopy() {
        String id = nikitaSession.getUser().getId();
        String login = nikitaSession.getUser().getLogin();
        CommandLine cmd = new CommandLine(commands);

        cmd.onUserInput(nikitaSession, RequestFactory.newRequest(id, login, "cd ../.."));
        cmd.onUserInput(nikitaSession, RequestFactory.newRequest(id, login, "mkdir applications/servers/weblogic"));
        cmd.onUserInput(nikitaSession, RequestFactory.newRequest(id, login, "mkdir logs"));
        cmd.onUserInput(nikitaSession, RequestFactory.newRequest(id, login, "copy applications logs"));

        Assertions.assertEquals(
                """
                        /
                        |__applications
                        |  |__servers
                        |  |  |__weblogic
                        |__home
                        |  |__nikita
                        |  |__r2d2
                        |__logs
                        |  |__applications
                        |  |  |__servers
                        |  |  |  |__weblogic
                        """,
                nodePrinter.print(nodeService.getRoot())
        );
    }

    @Test
    public void testMove() {
        String id = nikitaSession.getUser().getId();
        String login = nikitaSession.getUser().getLogin();
        CommandLine cmd = new CommandLine(commands);

        cmd.onUserInput(nikitaSession, RequestFactory.newRequest(id, login, "cd ../.."));
        cmd.onUserInput(nikitaSession, RequestFactory.newRequest(id, login, "mkdir applications/servers/weblogic"));
        cmd.onUserInput(nikitaSession, RequestFactory.newRequest(id, login, "mkdir logs"));
        cmd.onUserInput(nikitaSession, RequestFactory.newRequest(id, login, "move applications logs"));

        Assertions.assertEquals(
                """
                        /
                        |__home
                        |  |__nikita
                        |  |__r2d2
                        |__logs
                        |  |__applications
                        |  |  |__servers
                        |  |  |  |__weblogic
                        """,
                nodePrinter.print(nodeService.getRoot())
        );
    }

    @Test
    public void testRm() {
        String id = nikitaSession.getUser().getId();
        String login = nikitaSession.getUser().getLogin();
        CommandLine cmd = new CommandLine(commands);

        cmd.onUserInput(nikitaSession, RequestFactory.newRequest(id, login, "cd ../.."));
        cmd.onUserInput(nikitaSession, RequestFactory.newRequest(id, login, "mkdir applications/servers"));
        cmd.onUserInput(nikitaSession, RequestFactory.newRequest(id, login, "mkdir logs"));
        cmd.onUserInput(nikitaSession, RequestFactory.newRequest(id, login, "rm applications"));

        Assertions.assertEquals(
                """
                        /
                        |__home
                        |  |__nikita
                        |  |__r2d2
                        |__logs
                        """,
                nodePrinter.print(nodeService.getRoot())
        );
    }

    @Test
    public void testLockScenario() {
        String id1 = nikitaSession.getUser().getId();
        String login1 = nikitaSession.getUser().getLogin();
        String id2 = r2d2Session.getUser().getId();
        String login2 = r2d2Session.getUser().getLogin();
        CommandLine cmd1 = new CommandLine(commands);
        CommandLine cmd2 = new CommandLine(commands);

        cmd1.onUserInput(nikitaSession, RequestFactory.newRequest(id1, login1, "cd ../.."));
        cmd2.onUserInput(r2d2Session,   RequestFactory.newRequest(id2, login2, "cd ../.."));

        cmd1.onUserInput(nikitaSession, RequestFactory.newRequest(id1, login1, "mkfile applications/servers/weblogic/logs/weblogic.log"));
        cmd2.onUserInput(r2d2Session, RequestFactory.newRequest(id2, login2, "mkfile applications/databases/oracle/bin/oracle.exe"));

        Assertions.assertEquals(
                """
                        /
                        |__applications
                        |  |__databases
                        |  |  |__oracle
                        |  |  |  |__bin
                        |  |  |  |  |__oracle.exe
                        |  |__servers
                        |  |  |__weblogic
                        |  |  |  |__logs
                        |  |  |  |  |__weblogic.log
                        |__home
                        |  |__nikita
                        |  |__r2d2
                        """,
                nodePrinter.print(nodeService.getRoot())
        );

        cmd1.onUserInput(nikitaSession, RequestFactory.newRequest(id1, login1, "lock applications/databases"));

        Assertions.assertEquals(
                """
                /
                |__applications
                |  |__databases [Locked by nikita ]
                |  |  |__oracle
                |  |  |  |__bin
                |  |  |  |  |__oracle.exe
                |  |__servers
                |  |  |__weblogic
                |  |  |  |__logs
                |  |  |  |  |__weblogic.log
                |__home
                |  |__nikita
                |  |__r2d2
                """,
                nodePrinter.print(nodeService.getRoot())
        );

        cmd2.onUserInput(r2d2Session, RequestFactory.newRequest(id2, login2, "lock applications"));

        Assertions.assertEquals(
                """
                /
                |__applications [Locked by r2d2 ]
                |  |__databases [Locked by nikita ]
                |  |  |__oracle
                |  |  |  |__bin
                |  |  |  |  |__oracle.exe
                |  |__servers
                |  |  |__weblogic
                |  |  |  |__logs
                |  |  |  |  |__weblogic.log
                |__home
                |  |__nikita
                |  |__r2d2
                """,
                nodePrinter.print(nodeService.getRoot())
        );
    }

    @Test
    public void testRecursiveLockScenario() {
        String id1 = nikitaSession.getUser().getId();
        String login1 = nikitaSession.getUser().getLogin();
        String id2 = r2d2Session.getUser().getId();
        String login2 = r2d2Session.getUser().getLogin();
        CommandLine cmd1 = new CommandLine(commands);
        CommandLine cmd2 = new CommandLine(commands);

        cmd1.onUserInput(nikitaSession, RequestFactory.newRequest(id1, login1, "cd ../.."));
        cmd2.onUserInput(r2d2Session,   RequestFactory.newRequest(id2, login2, "cd ../.."));

        cmd1.onUserInput(nikitaSession, RequestFactory.newRequest(id1, login1, "mkfile applications/servers/weblogic/logs/weblogic.log"));
        cmd2.onUserInput(r2d2Session,   RequestFactory.newRequest(id2, login2, "mkfile applications/databases/oracle/bin/oracle.exe"));

        Assertions.assertEquals(
                """
                        /
                        |__applications
                        |  |__databases
                        |  |  |__oracle
                        |  |  |  |__bin
                        |  |  |  |  |__oracle.exe
                        |  |__servers
                        |  |  |__weblogic
                        |  |  |  |__logs
                        |  |  |  |  |__weblogic.log
                        |__home
                        |  |__nikita
                        |  |__r2d2
                        """,
                nodePrinter.print(nodeService.getRoot())
        );

        cmd1.onUserInput(nikitaSession, RequestFactory.newRequest(id1, login1, "lock -r applications/databases"));

        Assertions.assertEquals(
                """
                /
                |__applications
                |  |__databases [Locked by nikita ]
                |  |  |__oracle [Locked by nikita ]
                |  |  |  |__bin [Locked by nikita ]
                |  |  |  |  |__oracle.exe [Locked by nikita ]
                |  |__servers
                |  |  |__weblogic
                |  |  |  |__logs
                |  |  |  |  |__weblogic.log
                |__home
                |  |__nikita
                |  |__r2d2
                """,
                nodePrinter.print(nodeService.getRoot())
        );

        cmd2.onUserInput(r2d2Session, RequestFactory.newRequest(id2, login2, "lock -r applications"));

        Assertions.assertEquals(
                """
                /
                |__applications
                |  |__databases [Locked by nikita ]
                |  |  |__oracle [Locked by nikita ]
                |  |  |  |__bin [Locked by nikita ]
                |  |  |  |  |__oracle.exe [Locked by nikita ]
                |  |__servers
                |  |  |__weblogic
                |  |  |  |__logs
                |  |  |  |  |__weblogic.log
                |__home
                |  |__nikita
                |  |__r2d2
                """,
                nodePrinter.print(nodeService.getRoot())
        );
    }

    @Test
    public void testRename() {
        String id1 = nikitaSession.getUser().getId();
        String login1 = nikitaSession.getUser().getLogin();
        String id2 = r2d2Session.getUser().getId();
        String login2 = r2d2Session.getUser().getLogin();
        CommandLine cmd1 = new CommandLine(commands);
        CommandLine cmd2 = new CommandLine(commands);

        cmd1.onUserInput(nikitaSession, RequestFactory.newRequest(id1, login1, "cd ../.."));
        cmd2.onUserInput(r2d2Session,   RequestFactory.newRequest(id2, login2, "cd ../.."));

        cmd1.onUserInput(nikitaSession, RequestFactory.newRequest(id1, login1, "mkfile applications/servers/weblogic/logs/weblogic.log"));
        cmd1.onUserInput(nikitaSession, RequestFactory.newRequest(id1, login1, "lock -r applications/servers/weblogic"));

        cmd2.onUserInput(r2d2Session,   RequestFactory.newRequest(id2, login2, "rename applications/servers web-servers"));

        Assertions.assertEquals(
                """
                        /
                        |__applications
                        |  |__web-servers
                        |  |  |__weblogic [Locked by nikita ]
                        |  |  |  |__logs [Locked by nikita ]
                        |  |  |  |  |__weblogic.log [Locked by nikita ]
                        |__home
                        |  |__nikita
                        |  |__r2d2
                        """,
                nodePrinter.print(nodeService.getRoot())
        );

    }

}
