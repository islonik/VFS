package org.vfs.server.services;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.vfs.server.model.UserSession;
import org.vfs.server.network.ClientWriter;

/**
 * @author Lipatov Nikita
 */
public class UserSessionServiceTest {

    @Test
    public void testGetSession() {
        LockService lockService = new LockService();
        NodeManager nodeManager = new NodeManager(lockService);
        NodeService nodeService = new NodeService("/", lockService, nodeManager);
        nodeService.initDirs();
        UserSessionService userSessionService = new UserSessionService(nodeService, lockService);

        Assert.assertNull(userSessionService.getSession(""));

        // UserSession #1
        ClientWriter nikitaCWMock = Mockito.mock(ClientWriter.class);

        UserSession userSession = userSessionService.startSession(nikitaCWMock);
        userSessionService.attachUser(userSession.getUser().getId(), "nikita");

        Assert.assertNotNull(userSessionService.getSession(userSession.getUser().getId()));
    }

    @Test
    public void testStopSession() {
        LockService lockService = new LockService();
        NodeManager nodeManager = new NodeManager(lockService);
        NodeService nodeService = new NodeService("/", lockService, nodeManager);
        nodeService.initDirs();
        UserSessionService userSessionService = new UserSessionService(nodeService, lockService);

        // UserSession #1
        ClientWriter nikitaCWMock = Mockito.mock(ClientWriter.class);

        UserSession userSession = userSessionService.startSession(nikitaCWMock);
        userSessionService.attachUser(userSession.getUser().getId(), "nikita");

        Assert.assertEquals(1, userSessionService.getRegistry().size());
        userSessionService.stopSession(userSession.getUser().getId());
        Assert.assertEquals(0, userSessionService.getRegistry().size());
        userSessionService.stopSession(userSession.getUser().getId());
        Assert.assertEquals(0, userSessionService.getRegistry().size());
    }

}
