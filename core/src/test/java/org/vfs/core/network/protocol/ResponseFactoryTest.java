package org.vfs.core.network.protocol;

import org.junit.Assert;
import org.junit.Test;
import org.vfs.core.network.protocol.ResponseFactory;

/**
 * @author Lipatov Nikita
 */
public class ResponseFactoryTest {

    @Test
    public void testNewResponse() {
        Protocol.Response response = ResponseFactory.newResponse(Protocol.Response.ResponseType.OK, "Directory was created!");
        Assert.assertEquals(
                "code: OK\n" +
                        "message: \"Directory was created!\"\n",
                response.toString()
        );
    }

    @Test
    public void testNewResponseWithSpecificCode() {
        Protocol.Response response = ResponseFactory.newResponse(Protocol.Response.ResponseType.OK, "/home/nikita", "123456");
        Assert.assertEquals(
                "code: OK\n" +
                        "message: \"/home/nikita\"\n" +
                        "specificCode: \"123456\"\n",
                response.toString()
        );
    }
}
