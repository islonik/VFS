package org.vfs.client.model;

/**
 * User class.
 * @author Lipatov Nikita
 */
public class User
{
    private String id;
    private String login;

    public User(String id, String login)
    {
        this.id = id;
        this.login = login;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }
}
