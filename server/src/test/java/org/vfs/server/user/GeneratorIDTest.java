package org.vfs.server.user;

import org.junit.Assert;
import org.junit.Test;

/**
 * User: Lipatov Nikita
 */
public class GeneratorIDTest
{

    @Test
    public void testGenerator_testCase01()
    {
        GeneratorID generator = GeneratorID.getInstance();
        String id01 = generator.getNextId();
        Assert.assertNotNull(id01);
        String id02 = generator.getNextId();
        Assert.assertNotNull(id02);
        String id03 = generator.getNextId();
        Assert.assertNotNull(id03);

        Assert.assertNotEquals(id01, id02);
        Assert.assertNotEquals(id02, id03);
        Assert.assertNotEquals(id01, id03);
    }
}
