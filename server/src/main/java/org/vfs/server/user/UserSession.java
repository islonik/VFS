package org.vfs.server.user;

import java.util.*;

/**
 * @author Lipatov Nikita
 */
public class UserSession
{
    private UserService userService;
    private HashMap<String, UserCell> users = new HashMap<String, UserCell>();

    public UserSession(UserService userService)
    {
        this.userService = userService;
    }

    public boolean addCell(String login)
    {
        login = login.toLowerCase().trim();
        if(!this.users.containsKey(login))
        {
            UserCell userCell = userService.createCell(login);
            this.users.put(login, userCell);
            return true;
        }
        return false;
    }

    public boolean removeCell(String id, String login)
    {
        login = login.toLowerCase().trim();
        if(this.users.containsKey(login))
        {
            UserCell userCell = this.users.get(login);
            if(userCell.getUser().getId().equals(id))
            {
                this.users.remove(login);
                return true;
            }
        }
        return false;
    }

    public boolean updateCell(UserCell userCell)
    {
        if(users.containsKey(userCell.getUser().getLogin()))
        {
            users.put(userCell.getUser().getLogin(), userCell);
            return true;
        }
        return false;
    }

    public UserCell getUserCell(String login)
    {
        login = login.toLowerCase().trim();
        if(this.users.containsKey(login))
        {
            return this.users.get(login);
        }
        return null;
    }


}
