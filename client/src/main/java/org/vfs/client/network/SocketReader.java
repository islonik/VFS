package org.vfs.client.network;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

/**
 * SocketReader should listen socket(inputStream) and write message in queue
 * BlockingQueue should use non-blocking API.
 * @author Lipatov Nikita
 */
public class SocketReader
{
    private Socket socket;
    private BlockingQueue<String> toUserQueue;
    private DataInputStream dataInputStream;

    public SocketReader(Socket socket, BlockingQueue<String> queue) throws IOException
    {
        this.toUserQueue = queue;
        this.setSocket(socket);
    }

    public void setSocket(Socket socket) throws IOException
    {
        this.socket = socket;
        this.dataInputStream = new DataInputStream(socket.getInputStream());
    }

    public void run()
    {
        try
        {
            while (socket.isConnected())
            {
                try
                {
                    String serverMessage = dataInputStream.readUTF();
                    toUserQueue.put(serverMessage);
                }
                catch (IOException ioe)
                {
                    try
                    {
                        this.socket = NetworkManager.getInstance().getSocket();
                        this.dataInputStream = new DataInputStream(socket.getInputStream());
                    }
                    catch(IOException iioe)
                    {
                        System.err.println(iioe.getMessage());
                    }
                }
            }
        }
        catch(InterruptedException ie)
        {
            System.err.println("SocketReader.InterruptedException.Message=" + ie.getMessage());
        }

    }

}
