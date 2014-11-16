package org.vfs.client.network;

import org.vfs.core.network.protocol.Protocol;

import java.util.concurrent.BlockingQueue;

/**
 * BlockingQueue should use blocking API.
 *
 * @author Lipatov Nikita
 */
public class IncomingMessageListener {
    private BlockingQueue<Protocol.Response> toUserQueue;
    private IncomingMessageHandler handler;

    public IncomingMessageListener(BlockingQueue<Protocol.Response> queue, IncomingMessageHandler handler) {
        this.toUserQueue = queue;
        this.handler = handler;
    }

    public void run() {
        while (true) {
            try {
                Protocol.Response response = toUserQueue.take();

                handler.handle(response);
            } catch (InterruptedException e) {
                System.out.println("QueueReader " + e.getMessage());
            }
        }
    }
}
