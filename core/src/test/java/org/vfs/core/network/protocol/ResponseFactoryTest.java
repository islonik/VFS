package org.vfs.core.network.protocol;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vfs.core.network.protocol.Protocol.Response;

/**
 * @author Lipatov Nikita
 */
public class ResponseFactoryTest {

    @Test
    public void testNewResponse() {
        Response response = ResponseFactory.newResponse(Response.ResponseType.OK, "Directory was created!");
        Assertions.assertEquals(
                """
                code: OK
                message: "Directory was created!"
                """,
                response.toString()
        );
    }

    @Test
    public void testNewResponseWithSpecificCode() {
        Response response = ResponseFactory.newResponse(Response.ResponseType.OK, "/home/nikita", "123456");
        Assertions.assertEquals(
                """
                code: OK
                message: "/home/nikita"
                specificCode: "123456"
                """,
                response.toString()
        );
    }
}
