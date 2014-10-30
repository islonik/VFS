package org.vfs.core.network.protocol.xml;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Lipatov Nikita
 */
public class ResponseFactoryTest {
    public static final String TYPICAL_RESPONSE =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                    "<response>\n" +
                    "    <code>0</code>\n" +
                    "    <message>/home/nikita</message>\n" +
                    "</response>\n";

    public static final String SPECIFIC_RESPONSE =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                    "<response>\n" +
                    "    <code>0</code>\n" +
                    "    <message>/home/nikita</message>\n" +
                    "    <specificCode>123456</specificCode>\n" +
                    "</response>\n";

    @Test
    public void testNewResponse() {
        String xml = ResponseFactory.newResponse(Response.STATUS_OK, "/home/nikita");

        Assert.assertEquals(TYPICAL_RESPONSE, xml);
    }

    @Test
    public void testNewResponseWithSpecificCode() {
        String xml = ResponseFactory.newResponse(Response.STATUS_OK, "/home/nikita", "123456");

        Assert.assertEquals(SPECIFIC_RESPONSE, xml);
    }

}
