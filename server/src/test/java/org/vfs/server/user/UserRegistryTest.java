package org.vfs.server.user;

import org.junit.Assert;
import org.junit.Test;

/**
 * User: Lipatov Nikita
 */
public class UserRegistryTest
{

    @Test
    public void testUserRegistry_case01()
    {
        String login = "testUser";
        Assert.assertTrue(UserRegistry.getInstance().addUser(login));

        Assert.assertNotNull(UserRegistry.getInstance().getUser(login));
        
        Assert.assertTrue(UserRegistry.getInstance().removeUser(Long.toString(UserRegistry.getInstance().getUser(login).getId()), login));

        Assert.assertNull(UserRegistry.getInstance().getUser(login));
    }
}
