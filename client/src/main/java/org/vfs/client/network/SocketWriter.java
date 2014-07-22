package org.vfs.client.network;

import java.io.*;
import java.util.concurrent.BlockingQueue;

/**
 * SocketWriter should listen queue from user and write message to the server(through socket)
 * BlockingQueue should use non-blocking API.
 *
 * @author Lipatov Nikita
 */
public class SocketWriter {
    private final BlockingQueue<String> toServerQueue;
    private final NetworkManager networkManager;

    public SocketWriter(BlockingQueue<String> queue, NetworkManager networkManager) throws IOException {
        this.toServerQueue = queue;
        this.networkManager = networkManager;
    }

    public void run() {
        try {
            while (true) {
                try {
                    String message = this.toServerQueue.take();

                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(networkManager.getSocket().getOutputStream()));

                    bufferedWriter.write(message, 0, message.length());
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                } catch (IOException ioe) {
                    System.err.println("SocketWriter.IOException.Message=" + ioe.getMessage());
                    throw new RuntimeException(ioe);
                }
            }
        } catch (InterruptedException ie) {
            System.err.println("SocketWriter.InterruptedException.Message=" + ie.getMessage());
        }

    }

}
