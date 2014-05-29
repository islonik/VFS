package org.vfs.core.network.protocol.impl;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Lipatov Nikita
 */
@XmlRootElement(name = "user")
public class UserElement
{

    private String id = "";
    private String login = "";

    @XmlAttribute(required = true)
    public void setId(String userId)
    {
        this.id = userId;
    }

    public String getId()
    {
        return this.id;
    }

    @XmlAttribute(required = true)
    public void setLogin(String userLogin)
    {
        this.login = userLogin;
    }

    public String getLogin()
    {
        return this.login;
    }
}
