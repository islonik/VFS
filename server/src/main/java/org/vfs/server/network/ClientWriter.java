package org.vfs.server.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vfs.core.network.protocol.Protocol;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;

import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.channels.SelectionKey.OP_WRITE;

/**
 * @author Lipatov Nikita
 */
public class ClientWriter {
    private static final Logger log = LoggerFactory.getLogger(ClientWriter.class);

    private final SelectionKey key;
    private final BlockingQueue<Protocol.Response> toUsersQueue;

    public ClientWriter(SelectionKey key, BlockingQueue<Protocol.Response> toUsersQueue) {
        this.key = key;
        this.toUsersQueue = toUsersQueue;
    }

    public void send(Protocol.Response response) {
        try {
            key.attach(response);
            key.interestOps(OP_WRITE);

            Protocol.Response toWrite = (Protocol.Response) key.attachment();

            ByteBuffer writeBuffer = ByteBuffer.wrap(toWrite.toByteString().toByteArray());
            SocketChannel channel = (SocketChannel)key.channel();
            channel.write(writeBuffer);
            key.interestOps(OP_READ);

        } catch (IOException ioe) {
            System.err.println("ClientWriter = " + ioe.getMessage());
        }
    }
}
