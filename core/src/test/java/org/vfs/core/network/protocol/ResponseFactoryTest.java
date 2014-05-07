package org.vfs.core.network.protocol;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Lipatov Nikita
 */
public class ResponseFactoryTest
{
    private String request01 =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<response>\n" +
            "  <code>0</code>\n" +
            "  <message>You changed directory</message>\n" +
            "</response>";

    private String request02 =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<response>\n" +
            "  <code>0</code>\n" +
            "  <message />\n" +
            "</response>";

    private String message01 = "You changed directory";

    @Test
    public void testResponse__create_testCase01()
    {
        ResponseFactory factory = new ResponseFactory();
        Response response = factory.create(0, -1, message01);

        Assert.assertEquals(0, response.getCode());
        Assert.assertEquals(message01, response.getMessage());
        Assert.assertEquals
        (
            request01,
            response.toXml()
        );
    }

    @Test
    public void testResponse__parse_testCase01()
    {
        ResponseFactory factory = new ResponseFactory();
        Response response = factory.parse(request01);

        Assert.assertEquals(0, response.getCode());
        Assert.assertEquals(message01, response.getMessage());
        Assert.assertEquals
        (
            request01,
            response.toXml()
        );
    }

    @Test
    public void testResponse__parse_testCase02()
    {
        ResponseFactory factory = new ResponseFactory();
        Response response = factory.parse(request02);

        Assert.assertEquals(0, response.getCode());
        Assert.assertEquals("", response.getMessage());
        Assert.assertEquals
        (
            request02,
            response.toXml()
        );
    }
}
