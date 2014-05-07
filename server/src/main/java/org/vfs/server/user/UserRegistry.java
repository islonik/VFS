package org.vfs.server.user;

import java.util.*;

/**
 * @author Lipatov Nikita
 */
public class UserRegistry
{
    private static UserRegistry instance = new UserRegistry();
    private HashMap<String, User> users = new HashMap<String, User>();

    public static UserRegistry getInstance()
    {
        UserRegistry localInstance = instance;
        if(localInstance == null)
        {
            synchronized (UserRegistry.class)
            {
                localInstance = instance;
                if(localInstance == null)
                {
                    instance = localInstance = new UserRegistry();
                }
            }
        }
        return localInstance;
    }

    public static void cleanup()
    {
        instance = null;
    }

    public boolean addUser(String login)
    {
        login = login.toLowerCase().trim();
        if(!this.users.containsKey(login))
        {
            this.users.put(login, new User(login));
            return true;
        }
        return false;
    }

    public boolean removeUser(String id, String login)
    {
        login = login.toLowerCase().trim();
        if(this.users.containsKey(login))
        {
            User user = this.users.get(login);
            if(user.getId() == Long.parseLong(id))
            {
                this.users.remove(login);
                return true;
            }
        }
        return false;
    }

    public User getUser(String login)
    {
        login = login.toLowerCase().trim();
        if(this.users.containsKey(login))
        {
            return this.users.get(login);
        }
        return null;
    }


}
