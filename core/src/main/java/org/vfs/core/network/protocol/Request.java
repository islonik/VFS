package org.vfs.core.network.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.*;

/**
 * @author Lipatov Nikita
 */
@XmlRootElement(name = "request")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "user",
        "command"
})
public class Request
{
    private static final Logger log = LoggerFactory.getLogger(Request.class);

    @XmlElement(required = true)
    protected User user;
    @XmlElement(required = true)
    protected String command;

    public User getUser()
    {
        return user;
    }

    public void setUser(User value)
    {
        this.user = value;
    }

    public String getCommand()
    {
        return command;
    }

    public void setCommand(String value)
    {
        this.command = value;
    }

}
