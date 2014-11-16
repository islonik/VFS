package org.vfs.server.network;

import org.vfs.core.network.protocol.Protocol;

import java.io.*;

/**
 * @author Lipatov Nikita
 */
public class MessageReader {

    private final InputStream inputStream;

    public MessageReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public Protocol.Request read() throws IOException {
        return Protocol.Request.parseDelimitedFrom(inputStream);
    }
}
