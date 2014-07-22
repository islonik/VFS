package org.vfs.client.network;

import java.util.concurrent.BlockingQueue;

/**
 * BlockingQueue should use blocking API.
 *
 * @author Lipatov Nikita
 */
public class IncomingMessageListener {
    private BlockingQueue<String> toUserQueue;
    private IncomingMessageHandler handler;

    public IncomingMessageListener(BlockingQueue<String> queue, IncomingMessageHandler handler) {
        this.toUserQueue = queue;
        this.handler = handler;
    }

    public void run() {
        while (true) {
            try {
                String serverResponse = toUserQueue.take();

                handler.handle(serverResponse);
            } catch (InterruptedException e) {
                System.out.println("QueueReader " + e.getMessage());
            }
        }
    }
}
