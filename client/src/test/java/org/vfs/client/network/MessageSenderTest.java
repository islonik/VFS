package org.vfs.client.network;

import org.junit.Assert;
import org.junit.Test;
import org.vfs.core.network.protocol.User;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Lipatov Nikita
 */
public class MessageSenderTest {

    @Test
    public void testSend() throws Exception {

        BlockingQueue<String> queue = new ArrayBlockingQueue<String>(2);
        MessageSender messageSender = new MessageSender(queue);
        User user = new User("0", "nikita");

        Assert.assertTrue(messageSender.send(user, "connect nikita"));

        String requestXml = queue.take();

        Assert.assertEquals
        (
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<request>\n" +
            "    <user>\n" +
            "        <id>0</id>\n" +
            "        <login>nikita</login>\n" +
            "    </user>\n" +
            "    <command>connect nikita</command>\n" +
            "</request>\n", requestXml
        );

    }
}
