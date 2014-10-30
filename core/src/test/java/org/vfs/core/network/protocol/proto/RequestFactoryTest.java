package org.vfs.core.network.protocol.proto;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Lipatov Nikita
 */
public class RequestFactoryTest {

    @Test
    public void testNewRequest() {
        RequestProto.Request request = RequestFactory.newRequest("122", "nikita", "copy dir1 dir2");
        Assert.assertEquals(
                "user {\n" +
                        "  id: \"122\"\n" +
                        "  login: \"nikita\"\n" +
                        "}\n" +
                        "command: \"copy dir1 dir2\"\n",
                request.toString()
        );
    }

    @Test
    public void testNewRequestConnectRequest() {
        RequestProto.Request request = RequestFactory.newRequest("0", "nikita", "connect nikita");

        Assert.assertEquals(
                "user {\n" +
                        "  id: \"0\"\n" +
                        "  login: \"nikita\"\n" +
                        "}\n" +
                        "command: \"connect nikita\"\n",
                request.toString()
        );
    }
}
