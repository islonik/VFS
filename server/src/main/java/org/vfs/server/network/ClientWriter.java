package org.vfs.server.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vfs.core.network.protocol.Protocol;

import java.util.concurrent.BlockingQueue;

/**
 * @author Lipatov Nikita
 */
public class ClientWriter {
    private static final Logger log = LoggerFactory.getLogger(ClientWriter.class);

    private final BlockingQueue<Protocol.Response> toUsersQueue;

    public ClientWriter(BlockingQueue<Protocol.Response> toUsersQueue) {
        this.toUsersQueue = toUsersQueue;
    }

    public void send(Protocol.Response response) {
        toUsersQueue.add(response);
    }
}
