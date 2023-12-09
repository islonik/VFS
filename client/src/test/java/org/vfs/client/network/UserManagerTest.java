package org.vfs.client.network;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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

        Assertions.assertTrue(userManager.isAuthorized());
        Assertions.assertEquals("nikita", user1.getLogin());

        User user2 = User.newBuilder()
                .setId(VFSConstants.NEW_USER)
                .setLogin("r2d2")
                .build();

        userManager.setUser(user2);

        Assertions.assertTrue(userManager.isAuthorized());
        Assertions.assertEquals("r2d2", user2.getLogin());

        userManager.setUser(null);
        Assertions.assertFalse(userManager.isAuthorized());
    }
}
