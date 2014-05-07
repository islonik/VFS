package org.vfs.server.model;

import org.junit.Assert;
import org.junit.Test;
import org.vfs.server.model.impl.Directory;
import org.vfs.server.user.User;

import java.util.HashMap;

/**
 * User: Lipatov Nikita
 */
public class ContextTest
{

    @Test
    public void testCase_01()
    {
        User user = new User("nikita");
        HashMap<String, String> keys = new HashMap<String, String>();
        Context context = new Context(user, keys);

        Assert.assertNull(context.getCommand());
        Assert.assertNull(context.getArg1());
        Assert.assertNull(context.getArg2());
        Assert.assertNull(context.getArgN(3));
        Assert.assertNull(context.getArgByKey("42"));
    }

    @Test
    public void testCase_02()
    {
        Directory dir = NodeFactory.getFactory().createDirectory("testDir");
        User user = new User("nikita");
        user.setDirectory(dir);
        HashMap<String, String> keys = new HashMap<String, String>();
        keys.put("command", "mkdir");
        keys.put("arg1", "testDir2");
        Context context = new Context(user, keys);

        Assert.assertEquals("mkdir", context.getCommand());
        Assert.assertEquals("testDir2", context.getArg1());
        Assert.assertNull(context.getArg2());
        Assert.assertNull(context.getArgN(3));
    }
}
