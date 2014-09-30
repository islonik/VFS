package org.vfs.client.network;

import java.io.*;
import java.net.Socket;
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
    private volatile Socket socket;
    private volatile DataOutputStream dataOutputStream;

    public SocketWriter(BlockingQueue<String> queue, NetworkManager networkManager) throws IOException {
        this.toServerQueue = queue;
        this.networkManager = networkManager;
    }

    public void run() {
        try {

            while (true) {
                try {
                    String message = this.toServerQueue.take();

                    if(socket == null || !socket.equals(networkManager.getSocket())) {
                        socket = networkManager.getSocket();
                        dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    }

                    dataOutputStream.writeUTF(message);
                    dataOutputStream.flush();
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
