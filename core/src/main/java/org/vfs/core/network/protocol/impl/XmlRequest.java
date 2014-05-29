package org.vfs.core.network.protocol.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vfs.core.network.protocol.Request;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Lipatov Nikita
 */
@XmlRootElement(name = "request")
public class XmlRequest implements Request
{
    private static final Logger log = LoggerFactory.getLogger(XmlRequest.class);

    private UserElement userElement;
    private String command;

    @XmlElement(name = "user", required = true)
    public void setUserElement(UserElement userElement)
    {
        this.userElement = userElement;
    }

    public UserElement getUserElement()
    {
        return this.userElement;
    }

    public String getUserId()
    {
        return userElement.getId();
    }

    public String getUserLogin()
    {
        return userElement.getLogin();
    }

    @XmlElement(required = true)
    public void setCommand(String command)
    {
        this.command = command;
    }

    public String getCommand()
    {
        return command;
    }

    public String toXml()
    {
        XmlHelper xmlHelper = new XmlHelper();
        return xmlHelper.marshal(XmlRequest.class, this);
    }

}
