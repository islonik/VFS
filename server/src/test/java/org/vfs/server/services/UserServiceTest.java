package org.vfs.server.services;

import org.junit.Assert;
import org.junit.Test;
import org.vfs.core.network.protocol.User;
import org.vfs.server.model.UserSession;

/**
 * @author Lipatov Nikita
 */
public class UserServiceTest {

    @Test
    public void testGetSession() throws Exception {
        LockService lockService = new LockService();
        NodeManager nodeManager = new NodeManager();
        NodeService nodeService = new NodeService("/", lockService, nodeManager);
        UserService userService = new UserService(nodeService, lockService);

        Assert.assertNull(userService.getSession(""));
        UserSession userSession = userService.startSession();

        Assert.assertNotNull(userService.getSession(userSession.getUser().getId()));
    }

    @Test
    public void testStopSession() throws Exception {
        LockService lockService = new LockService();
        NodeManager nodeManager = new NodeManager();
        NodeService nodeService = new NodeService("/", lockService, nodeManager);
        UserService userService = new UserService(nodeService, lockService);

        UserSession userSession = userService.startSession();

        Assert.assertEquals(1, userService.getRegistry().size());
        userService.stopSession(userSession.getUser().getId());
        Assert.assertEquals(0, userService.getRegistry().size());
        userService.stopSession(userSession.getUser().getId());
        Assert.assertEquals(0, userService.getRegistry().size());
    }
}
