package org.vfs.client.network;

import org.vfs.core.network.protocol.Protocol;

import java.io.*;
import java.util.concurrent.BlockingQueue;

/**
 * SocketWriter should listen queue from user and write message to the server(through socket)
 * BlockingQueue should use non-blocking API.
 *
 * @author Lipatov Nikita
 */
public class SocketWriter {
    private final BlockingQueue<Protocol.Request> toServerQueue;
    private final NetworkManager networkManager;

    public SocketWriter(BlockingQueue<Protocol.Request> queue, NetworkManager networkManager) throws IOException {
        this.toServerQueue = queue;
        this.networkManager = networkManager;
    }

    public void run() {
        try {

            while (true) {
                try {
                    Protocol.Request request = this.toServerQueue.take();

                    OutputStream os = networkManager.getSocket().getOutputStream();
                    request.writeDelimitedTo(os);
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
