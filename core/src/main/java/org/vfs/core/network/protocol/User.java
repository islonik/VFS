package org.vfs.core.network.protocol;

import javax.xml.bind.annotation.*;

/**
 * @author Lipatov Nikita
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "User", propOrder = {
        "id",
        "login",
        "directory"
})
public class User
{
    @XmlElement(required = true)
    protected String id;
    @XmlElement(required = true)
    protected String login;
    @XmlElement(required = false)
    protected Object directory;

    public User()
    {

    }

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
        return this.id;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public String getLogin()
    {
        return this.login;
    }

    public Object getDirectory()
    {
        return directory;
    }

    public void setDirectory(Object directory)
    {
        this.directory = directory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        if (login != null ? !login.equals(user.login) : user.login != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (login != null ? login.hashCode() : 0);
        return result;
    }
}
