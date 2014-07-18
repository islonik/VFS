package org.vfs.client.network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

/**
 * SocketWriter should listen queue from user and write message to the server(through socket)
 * BlockingQueue should use non-blocking API.
 * @author Lipatov Nikita
 */
public class SocketWriter
{
    private Socket socket;
    private DataOutputStream outStream;
    private BlockingQueue<String> toServerQueue;

    public SocketWriter(Socket socket, BlockingQueue<String> queue) throws IOException
    {
        this.toServerQueue = queue;
        this.setSocket(socket);
    }

    public void setSocket(Socket socket) throws IOException
    {
        this.socket = socket;
        this.outStream = new DataOutputStream(this.socket.getOutputStream());
    }

    public void run()
    {
        try
        {
            while(socket.isConnected())
            {
                while(toServerQueue.isEmpty())
                {
                    Thread.currentThread().sleep(10); // or getting throw exception because no element
                }
                String message = toServerQueue.remove();
                outStream.writeUTF(message);
                outStream.flush();
            }
        }
        catch(IOException ioe)
        {
            System.err.println("SocketWriter.IOException.Message=" + ioe.getMessage());
        }
        catch(InterruptedException ie)
        {
            System.err.println("SocketWriter.IOException.Message=" + ie.getMessage());
        }

    }
}
