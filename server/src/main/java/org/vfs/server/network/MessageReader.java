package org.vfs.server.network;

import java.io.*;

/**
 * @author Lipatov Nikita
 */
public class MessageReader {

    private final DataInputStream dataInputStream;

    public MessageReader(InputStream inputStream)
    {
        this.dataInputStream = new DataInputStream(inputStream);
    }

    public String read() throws IOException
    {
        return dataInputStream.readUTF();
    }
}
