package org.vfs.client.model;

import org.vfs.core.network.protocol.User;

/**
 * @author Lipatov Nikita
 */
public class UserManager
{
    private static UserManager instance = new UserManager();

    private User user;

    public static UserManager getInstance()
    {
        return instance;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }
}
