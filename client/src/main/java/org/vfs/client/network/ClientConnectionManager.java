package org.vfs.client.network;

import java.io.IOException;

/**
 * @author Lipatov Nikita
 */
public class ClientConnectionManager implements AutoCloseable
{
    private static ClientConnectionManager instance;

    private ClientConnection clientConnection = null;

    public static void setInstance(ClientConnectionManager manager)
    {
        instance = manager;
    }

    public static ClientConnectionManager getInstance()
    {
        if(instance == null)
        {
            instance = new ClientConnectionManager();
        }
        return instance;
    }

    public ClientConnection createClientConnection(String serverHost, String serverPort) throws IOException
    {
        return new ClientConnection(serverHost, serverPort);
    }

    public ClientConnection getClientConnection()
    {
        return clientConnection;
    }

    public void setClientConnection(ClientConnection clientConnection)
    {
        this.clientConnection = clientConnection;
    }

    public void close()
    {
        if(this.clientConnection != null)
        {
            this.clientConnection.close();
        }
    }

}
