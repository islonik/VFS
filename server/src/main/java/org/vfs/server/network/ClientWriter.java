package org.vfs.server.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * @author Lipatov Nikita
 */
public class ClientWriter {
    private static final Logger log = LoggerFactory.getLogger(ClientWriter.class);

    private final Socket socket;
    private final OutputStream outputStream;
    private final BufferedWriter writer;

    public ClientWriter(Socket socket) throws IOException {
        this.socket = socket;
        this.outputStream = this.socket.getOutputStream();
        this.writer = new BufferedWriter(new OutputStreamWriter(this.outputStream));
    }

    public void send(String message) {
        try {
            if(!socket.isClosed()) {
                writer.write(message, 0, message.length());
                writer.newLine();
                writer.flush();
            }
        } catch (IOException ioe) {
            System.err.println("ClientWriter.IOException.Message=" + ioe.getMessage());
            throw new RuntimeException(ioe.getMessage(), ioe);
        }
    }
}
