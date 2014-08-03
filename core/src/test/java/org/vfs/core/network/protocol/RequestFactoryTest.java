package org.vfs.core.network.protocol;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Lipatov Nikita
 */
public class RequestFactoryTest {
    public static final String TYPICAL_REQUEST =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                    "<request>\n" +
                    "    <user>\n" +
                    "        <id>1234345</id>\n" +
                    "        <login>admin</login>\n" +
                    "    </user>\n" +
                    "    <command>cd applications</command>\n" +
                    "</request>\n";

    public static final String CONNECT_REQUEST =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                    "<request>\n" +
                    "    <user>\n" +
                    "        <id>0</id>\n" +
                    "        <login>nikita</login>\n" +
                    "    </user>\n" +
                    "    <command>connect nikita</command>\n" +
                    "</request>\n";

    @Test
    public void testNewRequest() {
        String xml = RequestFactory.newRequest("1234345", "admin", "cd applications");

        Assert.assertTrue(xml.contains("<command>cd applications</command>"));
        Assert.assertTrue(xml.contains("<id>1234345</id>"));
        Assert.assertTrue(xml.contains("<login>admin</login>"));
        Assert.assertEquals(TYPICAL_REQUEST, xml);
    }

    @Test
    public void testNewRequestConnectRequest() {
        String xml = RequestFactory.newRequest("0", "nikita", "connect nikita");

        Assert.assertTrue(xml.contains("<command>connect nikita</command>"));
        Assert.assertTrue(xml.contains("<id>0</id>"));
        Assert.assertTrue(xml.contains("<login>nikita</login>"));
        Assert.assertEquals(CONNECT_REQUEST, xml);
    }


}
