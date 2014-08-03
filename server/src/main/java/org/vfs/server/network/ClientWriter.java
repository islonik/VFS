package org.vfs.server.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vfs.server.model.UserSession;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.BlockingQueue;

/**
 * @author Lipatov Nikita
 */
public class ClientWriter {
    private static final Logger log = LoggerFactory.getLogger(ClientWriter.class);

    private final UserSession userSession;
    private final OutputStream outputStream;
    private final BufferedWriter writer;

    public ClientWriter(UserSession userSession) throws IOException {
        this.userSession = userSession;
        this.outputStream = userSession.getSocket().getOutputStream();
        this.writer = new BufferedWriter(new OutputStreamWriter(this.outputStream));
    }

    public void send(String message) {
        try {
            writer.write(message, 0, message.length());
            writer.newLine();
            writer.flush();

        } catch (IOException ioe) {
            System.err.println("ClientWriter.IOException.Message=" + ioe.getMessage());
            throw new RuntimeException(ioe);
        }
    }
}
