package org.vfs.server.user;

import org.junit.Assert;
import org.junit.Test;

/**
 * User: Lipatov Nikita
 */
public class UserSessionTest
{

    @Test
    public void testUserRegistry_case01()
    {
        String login = "testUser";
        Assert.assertTrue(UserSession.getInstance().addUser(login));

        Assert.assertNotNull(UserSession.getInstance().getUser(login));
        
        Assert.assertTrue(UserSession.getInstance().removeUser(UserSession.getInstance().getUser(login).getId(), login));

        Assert.assertNull(UserSession.getInstance().getUser(login));
    }
}
