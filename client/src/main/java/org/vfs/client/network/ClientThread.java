package org.vfs.client.network;

import org.vfs.client.model.Authorization;
import org.vfs.core.network.protocol.Response;
import org.vfs.core.network.protocol.ResponseService;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

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

    public ClientThread(String host, String port)
    {
        if (socket == null)
        {
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

                if(serverResponse == null)
                {
                    break;
                }

                ResponseService factory = new ResponseService();
                Response response = factory.parse(serverResponse);

                int code       = Integer.parseInt(response.getCode());
                String message = response.getMessage();

                Authorization authorization = Authorization.getInstance();

                if(Response.STATUS_SUCCESS_CONNECT == code && !authorization.isAuthorized())   // success authorization
                {
                    authorization.getUser().setId(response.getSpecificCode());
                }
                else if(Response.STATUS_FAIL_CONNECT == code && !authorization.isAuthorized()) // fail authorization
                {
                    authorization.setUser(null);
                    if(authorization.getConnection() != null)
                    {
                        authorization.getConnection().kill();
                    }
                }
                else if(Response.STATUS_SUCCESS_QUIT == code && authorization.isAuthorized())  // quit response
                {
                    authorization.setUser(null);
                    if(authorization.getConnection() != null)
                    {
                        authorization.getConnection().kill();
                    }
                    System.out.println(message);
                    break; // close current connection
                }

                System.out.println(message);
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
                socket.close();
            }
        }
        catch (IOException error)
        {
            System.err.println("Close method:" + error);
        }
    }
}
