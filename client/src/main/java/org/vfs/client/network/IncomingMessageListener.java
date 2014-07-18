package org.vfs.client.network;

import org.vfs.client.model.IncomingMessageHandler;

import java.util.concurrent.BlockingQueue;

/**
 * BlockingQueue should use blocking API.
 * @author Lipatov Nikita
 */
public class IncomingMessageListener
{
    private BlockingQueue<String> toUserQueue;
    private IncomingMessageHandler handler;

    public IncomingMessageListener(BlockingQueue<String> queue)
    {
        this.toUserQueue = queue;
        this.handler = new IncomingMessageHandler();
    }

    public void run()
    {
        while(true)
        {
            try
            {
                String serverResponse = toUserQueue.take();

                handler.handle(serverResponse);
            }
            catch(InterruptedException e)
            {
                System.out.println("QueueReader " + e.getMessage());
            }
        }
    }
}
