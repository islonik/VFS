package org.vfs.client.network;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
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
    private volatile Socket socket;
    private volatile DataInputStream dataInputStream;

    public SocketReader(BlockingQueue<String> queue, NetworkManager networkManager) throws IOException {
        this.toUserQueue = queue;
        this.networkManager = networkManager;
    }

    public void run() {
        try {
            while (true) {
                try {
                    if(socket == null || !socket.equals(networkManager.getSocket())) {
                        socket = networkManager.getSocket();
                        dataInputStream = new DataInputStream(socket.getInputStream());
                    }
                    String serverMessage = dataInputStream.readUTF();
                    toUserQueue.put(serverMessage);

                } catch (SocketException se) {
                    if(!se.getMessage().toLowerCase().equals("socket closed")) {
                        System.err.println("SocketReader.SocketException.Message=" + se.getMessage());
                    }
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
