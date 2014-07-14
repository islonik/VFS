package org.vfs.core.network.protocol;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Lipatov Nikita
 */
public class ResponseServiceTest
{
    private String request01 =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<response>\n" +
            "    <code>0</code>\n" +
            "    <message>You changed directory</message>\n" +
            "    <specificCode>0</specificCode>\n" +
            "</response>\n";

    private String request02 =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<response>\n" +
            "    <code>0</code>\n" +
            "    <message>You changed directory</message>\n" +
            "    <specificCode>3</specificCode>\n" +
            "</response>\n";

    private String request03 =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<response>\n" +
            "    <code>0</code>\n" +
            "    <message></message>\n" +
            "</response>\n";

    private String message01 = "You changed directory";

    @Test
    public void testResponse_create_testCase01()
    {
        ResponseService service = new ResponseService();
        Response response = service.create(0, 0, message01);

        Assert.assertEquals("0", response.getCode());
        Assert.assertEquals(message01, response.getMessage());
        Assert.assertEquals
        (
            request01,
            service.toXml(response)
        );
    }

    @Test
    public void testResponse_create_testCase02()
    {
        ResponseService service = new ResponseService();
        Response response = service.create(0, 3, message01);

        Assert.assertEquals("0", response.getCode());
        Assert.assertEquals(message01, response.getMessage());
        Assert.assertEquals("3", response.getSpecificCode());
        Assert.assertEquals
        (
            request02,
            service.toXml(response)
        );
    }

    @Test
    public void testResponse__parse_testCase01()
    {
        ResponseService service = new ResponseService();
        Response response = service.parse(request01);

        Assert.assertEquals("0", response.getCode());
        Assert.assertEquals(message01, response.getMessage());
        Assert.assertEquals
        (
            request01,
            service.toXml(response)
        );
    }

    @Test
    public void testResponse__parse_testCase02()
    {
        ResponseService service = new ResponseService();
        Response response = service.parse(request03);

        Assert.assertEquals("0", response.getCode());
        Assert.assertEquals("", response.getMessage());
        String xml = service.toXml(response);
        Assert.assertEquals(request03, xml);
    }

}
