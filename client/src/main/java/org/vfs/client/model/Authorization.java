package org.vfs.client.model;

import org.vfs.client.network.ClientThread;
import org.vfs.core.network.protocol.Request;
import org.vfs.core.network.protocol.RequestFactory;

/**
 * Class of user authorization.
 * @author Lipatov Nikita
 */
public class Authorization implements AutoCloseable
{
    private ClientThread client = null;  // Object of client connection.

    private User user = null;
    private String errorMessage = "";

    public ClientThread getConnection()
    {
        return client;
    }

    public String getErrorMessage()
    {
        return this.errorMessage;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public boolean isAuthorized()
    {
        if(user != null)
        {
            String id = user.getId().trim();
            return (id.isEmpty() || id.equals("0") || id.equals(" "))
                    ? false : true;
        }
        return false;
    }

    public void close() throws Exception
    {
        if(this.client != null)
        {
            this.client.kill();
        }
    }

    public boolean sendConnectCommand(String serverHost, String serverPort)
    {
        if(serverHost == null || serverPort == null || user == null)
        {
            this.errorMessage = "ServerHost or ServerPort or User doesn't found!";
            return false;
        }

        client = new ClientThread(this, serverHost, serverPort);

        if(!client.isConnected())
        {
            this.errorMessage = "Connection wasn't established! Please check host name and port!";
            return false;
        }

        RequestFactory factory = new RequestFactory();
        Request request = factory.create(user.getId(), user.getLogin(), "connect " + user.getLogin());

        String xml = request.toXml();
        client.flush(xml); // first command
        return true;
    }



}
