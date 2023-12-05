package org.vfs.client.network;

import org.junit.Assert;
import org.junit.Test;
import org.vfs.core.VFSConstants;
import org.vfs.core.network.protocol.Protocol.User;

/**
 * @author Lipatov Nikita
 */
public class UserManagerTest {

    @Test
    public void testSetUser() {

        UserManager userManager = new UserManager();

        User user1 = User.newBuilder()
                .setId(VFSConstants.NEW_USER)
                .setLogin("nikita")
                .build();

        userManager.setUser(user1);

        Assert.assertTrue(userManager.isAuthorized());
        Assert.assertEquals("nikita", user1.getLogin());

        User user2 = User.newBuilder()
                .setId(VFSConstants.NEW_USER)
                .setLogin("r2d2")
                .build();

        userManager.setUser(user2);

        Assert.assertTrue(userManager.isAuthorized());
        Assert.assertEquals("r2d2", user2.getLogin());

        userManager.setUser(null);
        Assert.assertFalse(userManager.isAuthorized());
    }
}
