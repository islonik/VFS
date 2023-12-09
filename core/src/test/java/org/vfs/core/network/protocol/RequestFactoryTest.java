package org.vfs.core.network.protocol;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vfs.core.VFSConstants;
import org.vfs.core.network.protocol.Protocol.Request;

/**
 * @author Lipatov Nikita
 */
public class RequestFactoryTest {

    @Test
    public void testNewRequest() {
        Request request = RequestFactory.newRequest("122", "nikita", "copy dir1 dir2");
        Assertions.assertEquals(
                """
                user {
                  id: "122"
                  login: "nikita"
                }
                command: "copy dir1 dir2"
                """,
                request.toString()
        );
    }

    @Test
    public void testNewRequestConnectRequest() {
        Request request = RequestFactory.newRequest(VFSConstants.NEW_USER, "nikita", "connect nikita");

        Assertions.assertEquals(
                """
                user {
                  id: "0"
                  login: "nikita"
                }
                command: "connect nikita"
                """,
                request.toString()
        );
    }
}
