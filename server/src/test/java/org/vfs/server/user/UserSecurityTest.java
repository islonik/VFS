package org.vfs.server.user;

import org.junit.Assert;
import org.junit.Test;
import org.vfs.core.network.protocol.Request;
import org.vfs.core.network.protocol.RequestFactory;

/**
 * @author Lipatov Nikita
 */
public class UserSecurityTest
{

    @Test
    public void testUserSecurity_testCase01()
    {
        RequestFactory factory = new RequestFactory();
        Request request = factory.create(" ", " ", "connect nikita");

        UserSecurity security = new UserSecurity();

        Assert.assertTrue(security.isSecure(request));


    }
}
