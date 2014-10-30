package org.vfs.core.network.protocol.proto;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Lipatov Nikita
 */
public class ResponseFactoryTest {

    @Test
    public void testNewResponse() {
        ResponseProto.Response response = ResponseFactory.newResponse(ResponseProto.Response.ResponseType.OK, "Directory was created!");
        Assert.assertEquals(
                "code: OK\n" +
                        "message: \"Directory was created!\"\n",
                response.toString()
        );
    }

    @Test
    public void testNewResponseWithSpecificCode() {
        ResponseProto.Response response = ResponseFactory.newResponse(ResponseProto.Response.ResponseType.OK, "/home/nikita", "123456");
        Assert.assertEquals(
                "code: OK\n" +
                        "message: \"/home/nikita\"\n" +
                        "specificCode: \"123456\"\n",
                response.toString()
        );
    }
}
