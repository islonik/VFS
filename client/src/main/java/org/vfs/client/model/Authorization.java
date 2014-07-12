package org.vfs.client.model;

import org.vfs.client.network.ClientThread;
import org.vfs.core.network.protocol.User;

import java.io.IOException;

/**
 * Class of user authorization and ClientThread which linked to user.
 * @author Lipatov Nikita
 */
public class Authorization implements AutoCloseable
{
    private static Authorization authorization = new Authorization();

    private User user;
    private ClientThread client;

    public static Authorization newInstance()
    {
        authorization = new Authorization();
        return authorization;
    }

    public static Authorization getInstance()
    {
        return authorization;
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

    public ClientThread getConnection()
    {
        return client;
    }

    public void setConnection(ClientThread clientThread)
    {
        this.client = clientThread;
    }

    public void close() throws IOException
    {
        if(this.client != null)
        {
            this.client.kill();
        }
    }

}
