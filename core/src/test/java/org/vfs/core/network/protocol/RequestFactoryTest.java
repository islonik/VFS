package org.vfs.core.network.protocol;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Lipatov Nikita
 */
public class RequestFactoryTest
{

    private String request01 =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<request>\n" +
        "  <user id=\"1234345\" login=\"admin\" />\n" +
        "  <command>cd applications</command>\n" +
        "</request>";

    private String request02 =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<request>\n" +
        "  <user id=\"1234345\" login=\"admin\" />\n" +
        "  <command>cd ..</command>\n" +
        "</request>";

    private String command01 = "cd applications";
    private String command02 = "cd ..";

    @Test
    public void testRequest__create_testCase01()
    {

        RequestFactory factory = new RequestFactory();
        Request request = factory.create("1234345", "admin", command01);

        Assert.assertEquals("1234345", request.getUserId());
        Assert.assertEquals("admin", request.getUserLogin());
        Assert.assertEquals(command01, request.getCommand());
        Assert.assertEquals
        (
            request01,
            request.toXml()
        );
    }

    @Test
    public void testRequest__parse_testCase01()
    {
        RequestFactory factory = new RequestFactory();
        Request request = factory.parse(request01);

        Assert.assertEquals("1234345", request.getUserId());
        Assert.assertEquals("admin", request.getUserLogin());
        Assert.assertEquals(command01, request.getCommand());
        Assert.assertEquals
        (
            request01,
            request.toXml()
        );
    }

    @Test
    public void testRequest__create_testCase02()
    {

        RequestFactory factory = new RequestFactory();
        Request request = factory.create("1234345", "admin", command02);

        Assert.assertEquals("1234345", request.getUserId());
        Assert.assertEquals("admin", request.getUserLogin());
        Assert.assertEquals(command02, request.getCommand());
        Assert.assertEquals
        (
            request02,
            request.toXml()
        );
    }
}
