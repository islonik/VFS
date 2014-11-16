package org.vfs.server.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vfs.core.network.protocol.Protocol;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * @author Lipatov Nikita
 */
public class ClientWriter {
    private static final Logger log = LoggerFactory.getLogger(ClientWriter.class);

    private final OutputStream outputStream;

    private final Socket socket;

    public ClientWriter(Socket socket) throws IOException {
        this.socket = socket;
        this.outputStream = this.socket.getOutputStream();
    }

    public void send(Protocol.Response response) {
        try {
            if(!socket.isClosed()) {
                response.writeDelimitedTo(outputStream);
           }
        } catch(SocketException se) {
            Thread.currentThread().interrupt(); // socket was closed, we should kill this thread
        } catch (IOException ioe) {
            System.err.println("ClientWriter.IOException.Message=" + ioe.getMessage());
            throw new RuntimeException(ioe.getMessage(), ioe);
        }
    }
}
