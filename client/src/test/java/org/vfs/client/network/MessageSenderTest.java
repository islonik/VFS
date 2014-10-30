package org.vfs.client.network;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.vfs.core.network.protocol.proto.RequestProto;
import org.vfs.core.network.protocol.proto.RequestProto.Request.User;

/**
 * @author Lipatov Nikita
 */
public class MessageSenderTest {

    @Test
    public void testSend() throws Exception {

        BlockingQueue<RequestProto.Request> queue = new ArrayBlockingQueue<>(2);
        MessageSender messageSender = new MessageSender(queue);

        User user = RequestProto.Request.User.newBuilder()
                .setId("0")
                .setLogin("nikita")
                .build();
        Assert.assertTrue(messageSender.send(user, "connect nikita"));

        RequestProto.Request request = queue.take();

        Assert.assertEquals
                (
                        "user {\n" +
                                "  id: \"0\"\n" +
                                "  login: \"nikita\"\n" +
                                "}\n" +
                                "command: \"connect nikita\"\n",
                        request.toString()
                );

    }
}
