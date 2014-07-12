package org.vfs.core.network.protocol;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Lipatov Nikita
 */
public class RequestServiceTest
{
    private String request01 =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n" +
        "<request>\n" +
        "  <user>\n" +
        "    <id>1234345</id>\n" +
        "    <login>admin</login>\n" +
        "  </user>\n" +
        "  <command>cd applications</command>\n" +
        "</request>\n";

    private String request02 =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n" +
        "<request>\n" +
        "  <user>\n" +
        "    <id>0</id>\n" +
        "    <login>nikita</login>\n" +
        "  </user>\n" +
        "  <command>connect nikita</command>\n" +
        "</request>\n";

    private String command01 = "cd applications";
    private String command02 = "cd ..";
    private String command03 = "connect nikita";

    @Test
    public void testRequest_create_testCase01()
    {
        RequestService requestService = new RequestService();
        Request request = requestService.create("1234345", "admin", command01);

        Assert.assertEquals("1234345", request.getUser().getId());
        Assert.assertEquals("admin",   request.getUser().getLogin());
        Assert.assertEquals(command01, request.getCommand());

        String xml = requestService.toXml(request);
        Assert.assertTrue(xml.contains("<command>cd applications</command>"));
        Assert.assertTrue(xml.contains("<id>1234345</id>"));
        Assert.assertTrue(xml.contains("<login>admin</login>"));
    }

    @Test
    public void testRequest_create_testCase02()
    {
        RequestService requestService = new RequestService();
        Request request = requestService.create("1234345", "admin", command02);

        Assert.assertEquals("1234345", request.getUser().getId());
        Assert.assertEquals("admin",   request.getUser().getLogin());
        Assert.assertEquals(command02, request.getCommand());

        String xml = requestService.toXml(request);
        Assert.assertTrue(xml.contains("<command>cd ..</command>"));
        Assert.assertTrue(xml.contains("<id>1234345</id>"));
        Assert.assertTrue(xml.contains("<login>admin</login>"));
    }

    @Test
    public void testRequest_parse_testCase01()
    {
        RequestService requestService = new RequestService();
        Request request = requestService.parse(request01);

        Assert.assertEquals("1234345", request.getUser().getId());
        Assert.assertEquals("admin",   request.getUser().getLogin());
        Assert.assertEquals(command01, request.getCommand());

        String xml = requestService.toXml(request);
        Assert.assertTrue(xml.contains("<command>cd applications</command>"));
        Assert.assertTrue(xml.contains("<id>1234345</id>"));
        Assert.assertTrue(xml.contains("<login>admin</login>"));
    }

    @Test
    public void testRequest_parse_testCase02()
    {
        RequestService requestService = new RequestService();
        Request request = requestService.parse(request02);

        Assert.assertEquals("0",       request.getUser().getId());
        Assert.assertEquals("nikita",  request.getUser().getLogin());
        Assert.assertEquals(command03, request.getCommand());

        String xml = requestService.toXml(request);
        Assert.assertTrue(xml.contains("<command>connect nikita</command>"));
        Assert.assertTrue(xml.contains("<id>0</id>"));
        Assert.assertTrue(xml.contains("<login>nikita</login>"));
    }


}
