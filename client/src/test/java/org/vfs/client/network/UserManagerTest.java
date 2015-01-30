package org.vfs.client.network;

import org.junit.Assert;
import org.junit.Test;
import org.vfs.core.network.protocol.Protocol;

/**
 * @author Lipatov Nikita
 */
public class UserManagerTest {

    @Test
    public void testSetUser() {

        UserManager userManager = new UserManager();

        Protocol.User user1 = Protocol.User.newBuilder()
                .setId("0")
                .setLogin("nikita")
                .build();

        userManager.setUser(user1);

        Assert.assertTrue(userManager.isAuthorized());
        Assert.assertEquals("nikita", user1.getLogin());

        Protocol.User user2 = Protocol.User.newBuilder()
                .setId("0")
                .setLogin("r2d2")
                .build();

        userManager.setUser(user2);

        Assert.assertTrue(userManager.isAuthorized());
        Assert.assertEquals("r2d2", user2.getLogin());

        userManager.setUser(null);
        Assert.assertFalse(userManager.isAuthorized());
    }
}
