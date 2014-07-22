package org.vfs.server.user;

import org.junit.Assert;
import org.junit.Test;
import org.vfs.core.network.protocol.Request;
import org.vfs.core.network.protocol.RequestFactory;

/**
 * @author Lipatov Nikita
 */
public class UserServiceTest
{

    @Test
    public void testUserService_testCase01()
    {
        RequestFactory factory = new RequestFactory();
        Request request = factory.create(" ", " ", "connect nikita");

        UserService service = new UserService();

        Assert.assertTrue(service.isSecure(request));


    }
}
