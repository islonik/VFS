package org.vfs.core.network.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.*;

/**
 * @author Lipatov Nikita
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "code",
        "message",
        "specificCode"
})
@XmlRootElement(name = "response")
public class Response
{
    public static final int STATUS_OK = 0;
    public static final int STATUS_FAIL = 1;
    public static final int STATUS_SUCCESS_CONNECT = 2;
    public static final int STATUS_FAIL_CONNECT = 3;
    public static final int STATUS_SUCCESS_QUIT = 4;
    public static final int STATUS_FAIL_QUIT = 5;

    private static final Logger log = LoggerFactory.getLogger(Response.class);

    @XmlElement(required = true)
    protected String code;
    @XmlElement(required = true)
    protected String message;
    @XmlElement(defaultValue = "0", required = false)
    protected String specificCode;

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }

    public void setSpecificCode(String specificCode)
    {
        this.specificCode = specificCode;
    }

    public String getSpecificCode()
    {
        return specificCode;
    }

}
