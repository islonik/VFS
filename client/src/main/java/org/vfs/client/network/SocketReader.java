package org.vfs.client.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

/**
 * SocketReader should listen socket(inputStream) and write message in queue
 * BlockingQueue should use non-blocking API.
 *
 * @author Lipatov Nikita
 */
public class SocketReader {
    private final BlockingQueue<String> toUserQueue;
    private final NetworkManager networkManager;

    public SocketReader(BlockingQueue<String> queue, NetworkManager networkManager) throws IOException {
        this.toUserQueue = queue;
        this.networkManager = networkManager;
    }

    public void run() {
        try {
            while (true) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(networkManager.getSocket().getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    while (bufferedReader.ready()) {
                        stringBuilder.append(bufferedReader.readLine());
                    }
                    if (stringBuilder.length() == 0) {
                        continue;
                    }
                    this.toUserQueue.put(stringBuilder.toString());
                } catch (IOException ioe) {
                    System.err.println("SocketReader.IOException.Message=" + ioe.getMessage());
                    throw new RuntimeException(ioe);
                }
            }
        } catch (InterruptedException ie) {
            System.err.println("SocketReader.InterruptedException.Message=" + ie.getMessage());
        }
    }

}
