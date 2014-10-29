package org.vfs.client.network;

import org.vfs.core.network.protocol.proto.ResponseProto;

import java.util.concurrent.BlockingQueue;

/**
 * BlockingQueue should use blocking API.
 *
 * @author Lipatov Nikita
 */
public class IncomingMessageListener {
    private BlockingQueue<ResponseProto.Response> toUserQueue;
    private IncomingMessageHandler handler;

    public IncomingMessageListener(BlockingQueue<ResponseProto.Response> queue, IncomingMessageHandler handler) {
        this.toUserQueue = queue;
        this.handler = handler;
    }

    public void run() {
        while (true) {
            try {
                ResponseProto.Response response = toUserQueue.take();

                handler.handle(response);
            } catch (InterruptedException e) {
                System.out.println("QueueReader " + e.getMessage());
            }
        }
    }
}
