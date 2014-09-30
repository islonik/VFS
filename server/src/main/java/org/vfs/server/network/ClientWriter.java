package org.vfs.server.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * @author Lipatov Nikita
 */
public class ClientWriter {
    private static final Logger log = LoggerFactory.getLogger(ClientWriter.class);

    private final Socket socket;
    private final DataOutputStream dataOutputStream;

    public ClientWriter(Socket socket) throws IOException {
        this.socket = socket;
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }

    public void send(String message) {
        try {
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush();
        } catch(SocketException se) {
            Thread.currentThread().interrupt(); // socket was closed, we should kill this thread
        } catch (IOException ioe) {
            System.err.println("ClientWriter.IOException.Message=" + ioe.getMessage());
            throw new RuntimeException(ioe.getMessage(), ioe);
        }
    }
}
