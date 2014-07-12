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
}
