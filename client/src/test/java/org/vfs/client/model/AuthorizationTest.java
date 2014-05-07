package org.vfs.client.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Lipatov Nikita
 */
public class AuthorizationTest
{

    @Test
    public void testAuthorization_testCase01()
    {
        User user = new User("0", "nikita");
        Authorization authorization = new Authorization();
        authorization.setUser(user);

        Assert.assertFalse(authorization.isAuthorized());
    }

    @Test
    public void testAuthorization_testCase02()
    {
        User user = new User("1", "nikita");
        Authorization authorization = new Authorization();
        authorization.setUser(user);

        Assert.assertTrue(authorization.isAuthorized());
    }

    @Test
    public void testAuthorization_testCase03()
    {
        User user = new User(" ", "nikita");
        Authorization authorization = new Authorization();
        authorization.setUser(user);

        Assert.assertFalse(authorization.isAuthorized());
    }

    @Test
    public void testAuthorization_testCase04()
    {
        User user = new User("", "nikita");
        Authorization authorization = new Authorization();
        authorization.setUser(user);

        Assert.assertFalse(authorization.isAuthorized());
    }
}
