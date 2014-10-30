package org.vfs.core.network.protocol.xml;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Lipatov Nikita
 */
public class XmlHelperTest {

    @Test
    public void testUnmarshalTypicalRequest() throws Exception {

        XmlHelper xmlHelper = new XmlHelper();
        Request typicalRequest = xmlHelper.unmarshal(Request.class, RequestFactoryTest.TYPICAL_REQUEST);

        Assert.assertEquals("1234345", typicalRequest.getUser().getId());
        Assert.assertEquals("admin", typicalRequest.getUser().getLogin());
        Assert.assertEquals("cd applications", typicalRequest.getCommand());
    }

    @Test
    public void testUnmarshalTypicalResponse() throws Exception {

        XmlHelper xmlHelper = new XmlHelper();
        Response typicalResponse = xmlHelper.unmarshal(Response.class, ResponseFactoryTest.TYPICAL_RESPONSE);

        Assert.assertEquals(0, typicalResponse.getCode());
        Assert.assertEquals("/home/nikita", typicalResponse.getMessage());
    }
}
