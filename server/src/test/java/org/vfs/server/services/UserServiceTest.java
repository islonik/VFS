package org.vfs.server.services;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.vfs.server.model.Timer;
import org.vfs.server.model.UserSession;
import org.vfs.server.network.ClientWriter;

import java.net.Socket;

/**
 * @author Lipatov Nikita
 */
public class UserServiceTest {

    @Test
    public void testGetSession() throws Exception {
        LockService lockService = new LockService();
        NodeManager nodeManager = new NodeManager();
        NodeService nodeService = new NodeService("/", lockService, nodeManager);
        nodeService.initDirs();
        UserService userService = new UserService(nodeService, lockService);

        Assert.assertNull(userService.getSession(""));

        // UserSession #1
        Socket nikitaSocketMock = Mockito.mock(Socket.class);
        Mockito.when(nikitaSocketMock.isClosed()).thenReturn(true);
        Timer nikitaTimerMock = Mockito.mock(Timer.class);
        Mockito.when(nikitaTimerMock.difference()).thenReturn(15);
        ClientWriter nikitaCWMock = Mockito.mock(ClientWriter.class);

        UserSession userSession = userService.startSession(nikitaSocketMock, nikitaTimerMock, nikitaCWMock);
        userService.attachUser(userSession.getUser().getId(), "nikita");

        Assert.assertNotNull(userService.getSession(userSession.getUser().getId()));
    }

    @Test
    public void testStopSession() throws Exception {
        LockService lockService = new LockService();
        NodeManager nodeManager = new NodeManager();
        NodeService nodeService = new NodeService("/", lockService, nodeManager);
        nodeService.initDirs();
        UserService userService = new UserService(nodeService, lockService);

        // UserSession #1
        Socket nikitaSocketMock = Mockito.mock(Socket.class);
        Mockito.when(nikitaSocketMock.isClosed()).thenReturn(true);
        Timer nikitaTimerMock = Mockito.mock(Timer.class);
        Mockito.when(nikitaTimerMock.difference()).thenReturn(15);
        ClientWriter nikitaCWMock = Mockito.mock(ClientWriter.class);

        UserSession userSession = userService.startSession(nikitaSocketMock, nikitaTimerMock, nikitaCWMock);
        userService.attachUser(userSession.getUser().getId(), "nikita");

        Assert.assertEquals(1, userService.getRegistry().size());
        userService.stopSession(userSession.getUser().getId());
        Assert.assertEquals(0, userService.getRegistry().size());
        userService.stopSession(userSession.getUser().getId());
        Assert.assertEquals(0, userService.getRegistry().size());
    }
}
