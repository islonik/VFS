package org.vfs.server.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Lipatov Nikita
 */
public class MessageReader {

    private final InputStream inputStream;
    private final BufferedReader reader;

    public MessageReader(InputStream inputStream)
    {
        this.inputStream = inputStream;
        reader = new BufferedReader(new InputStreamReader(this.inputStream));
    }

    public String read() throws IOException
    {
        StringBuilder stringBuilder = new StringBuilder();
        while(stringBuilder.length() <= 0)
        {
            while(reader.ready())
            {
                stringBuilder.append(reader.readLine());
            }
        }
        return stringBuilder.toString();
    }
}
