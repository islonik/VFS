package org.vfs.client.model;

import org.vfs.client.network.ClientThread;
import org.vfs.core.network.protocol.Request;
import org.vfs.core.network.protocol.RequestFactory;

/**
 * Class of user authorization.
 * @author Lipatov Nikita
 */
public class Authorization
{

    private static String serverHost = null;        // Name of server;
    private static String serverPort = null;        // Port of server;

    private static ClientThread client = null;  // Object of client connection.

    private static User user = null;
    private String errorMessage = "";

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public ClientThread getConnection()
    {
        return client;
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

    public void setServerPort(String port)
    {
        serverPort = port;
    }

    public void setServerHost(String host)
    {
        serverHost = host;
    }

    public boolean sendConnectCommand()
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

    public String getErrorMessage()
    {
        return this.errorMessage;
    }

}
