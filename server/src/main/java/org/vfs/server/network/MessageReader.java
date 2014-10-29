package org.vfs.server.network;

import org.vfs.core.network.protocol.proto.RequestProto;

import java.io.*;

/**
 * @author Lipatov Nikita
 */
public class MessageReader {

    private final InputStream inputStream;

    public MessageReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public RequestProto.Request read() throws IOException {
        return RequestProto.Request.parseDelimitedFrom(inputStream);
    }
}
