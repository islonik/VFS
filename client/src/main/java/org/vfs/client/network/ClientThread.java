package org.vfs.client.network;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import org.vfs.client.model.Authorization;
import org.vfs.client.model.CommandParser;

/**
 * Class of client connection.
 * @author Lipatov Nikita
 */
public class ClientThread extends Thread
{

    private DataInputStream inStream = null;    // stream from server;
    private DataOutputStream outStream = null;  // stream from user;
    private Socket socket = null;               // socket;
    private String serverHost = null;           // name of server;
    private String serverPort = null;           // port of server;
    private Thread listener = null;             // thread;
    private Authorization authorization;

    public ClientThread(Authorization authorization, String host, String port)
    {
        if (socket == null)
        {
            this.authorization = authorization;
            serverHost = host;
            serverPort = port;
            listener = new Thread(this);
            createSocket();
            listener.start();
        }
    }

    /**
     * Method creates the socket and streams.
     */
    private void createSocket()
    {
        try
        {
            InetAddress ipAddress = InetAddress.getByName(serverHost);
            socket    = new Socket(ipAddress, Integer.parseInt(serverPort));
            inStream  = new DataInputStream(socket.getInputStream());
            outStream = new DataOutputStream(socket.getOutputStream());
        }
        catch (IOException error)
        {
            System.err.println("CreateSocketError = " + error.getLocalizedMessage());
            kill();
        }
    }

    public boolean isConnected()
    {
        return socket.isConnected();
    }

    /**
     * Method sends messages / commands to server.
     * @param message Message from user / to server.
     */
    public void flush(String message)
    {
        try
        {
            outStream.writeUTF(message);
            outStream.flush();
        }
        catch (IOException error)
        {
            System.err.println("flush method: " + error);
        }
    }

    @Override
    public void run()
    {
        try
        {
            while (true)
            {
                // server response
                String serverResponse = inStream.readUTF();

                CommandParser parser = new CommandParser(this.authorization);
                if(!parser.parseServerResponse(serverResponse))
                {
                    break;
                }
            }
        }
        catch (IOException error)
        {
            System.err.println("Connection to the server was lost");
        }
        finally
        {
            kill();
        }
    }

    /**
     * Method kills the object of client connection.
     */
    public void kill()
    {
        try
        {
            if(socket != null)
            {
                if(!socket.isClosed())
                {
                    socket.close();
                }
            }
            this.interrupt();
        }
        catch (Exception error)
        {
            System.err.println("Close method:" + error);
        }
    }
}
