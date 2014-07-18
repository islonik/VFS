package org.vfs.client.network;

import org.vfs.client.model.UserManager;
import org.vfs.core.network.protocol.Response;
import org.vfs.core.network.protocol.ResponseService;
import org.vfs.core.network.protocol.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @author Lipatov Nikita
 */
public class ClientConnection extends Thread
{
    private Thread listener = null;      // thread;
    private Socket socket;               // socket;
    private DataInputStream inStream;    // stream from server;
    private DataOutputStream outStream;  // stream from user;

    public ClientConnection(String serverHost, String serverPort) throws IOException
    {
        listener = new Thread(this);
        InetAddress ipAddress = InetAddress.getByName(serverHost);
        socket    = new Socket(ipAddress, Integer.parseInt(serverPort));
        inStream  = new DataInputStream(socket.getInputStream());
        outStream = new DataOutputStream(socket.getOutputStream());
        listener.start();
    }

    public boolean isConnected()
    {
        if(socket != null && inStream != null && outStream != null)
        {
            if(socket.isConnected())
            {
                return true;
            }
        }
        return false;
    }

    public void sendMessageToServer(String message)
    {
        try
        {
            outStream.writeUTF(message);
            outStream.flush();
        }
        catch (IOException error)
        {
            System.err.println("sendMessageToServer method: " + error.getLocalizedMessage());
        }
    }

    public void run()
    {
        try
        {
            while (true)
            {
                // server response
                String serverResponse = inStream.readUTF();

                ResponseService responseService = new ResponseService();
                Response response = responseService.parse(serverResponse);

                int code       = Integer.parseInt(response.getCode());
                String message = response.getMessage();

                UserManager userManager = UserManager.getInstance();
                User user = userManager.getUser();
                if(Response.STATUS_SUCCESS_CONNECT == code && user == null)   // success authorization
                {
                    user.setId(response.getSpecificCode());
                    userManager.setUser(user);
                }
                else if(Response.STATUS_FAIL_CONNECT == code && user == null) // fail authorization
                {
                    UserManager.getInstance().setUser(null);
                    ClientConnectionManager.getInstance().setClientConnection(null);
                }
                else if(Response.STATUS_SUCCESS_QUIT == code && user != null)  // quit response
                {
                    UserManager.getInstance().setUser(null);
                    ClientConnectionManager.getInstance().close();
                    ClientConnectionManager.getInstance().setClientConnection(null);
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
            close();
        }
    }

    public void close()
    {
        try
        {
            if(socket != null)
            {
                socket.close();
            }
        }
        catch (IOException ier)
        {
            System.err.println(ier.getLocalizedMessage());
        }
    }

}
