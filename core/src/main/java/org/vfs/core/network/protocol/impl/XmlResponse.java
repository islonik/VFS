package org.vfs.core.network.protocol.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vfs.core.network.protocol.Response;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Lipatov Nikita
 */
@XmlRootElement (name = "response")
public class XmlResponse implements Response
{
    private static final Logger log = LoggerFactory.getLogger(XmlResponse.class);

    protected String message;
    protected String code;
    protected String specificCode;

    @XmlElement(required = true)
    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }

    @XmlElement(required = true)
    public void setCode(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }

    @XmlElement(defaultValue = "0", required = false)
    public void setSpecificCode(String specificCode)
    {
        this.specificCode = specificCode;
    }

    public String getSpecificCode()
    {
        return specificCode;
    }

    public String toXml()
    {
        XmlHelper xmlHelper = new XmlHelper();
        return xmlHelper.marshal(XmlResponse.class, this);
    }

}
