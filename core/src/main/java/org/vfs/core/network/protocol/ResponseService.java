package org.vfs.core.network.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lipatov Nikita
 */
public class ResponseService
{

    private static final Logger log = LoggerFactory.getLogger(ResponseService.class);

    public Response create(int code, long specificCode, String message)
    {
        Response response = new Response();
        response.setMessage(message);
        response.setCode(Integer.toString(code));
        response.setSpecificCode(Long.toString(specificCode));

        return response;
    }

    public Response parse(String xmlResponse)
    {
        XmlHelper xmlHelper = new XmlHelper();

        return (Response) xmlHelper.unmarshal(Response.class, xmlResponse);
    }

    public String toXml(Response response)
    {
        XmlHelper xmlHelper = new XmlHelper();
        return xmlHelper.marshal(Response.class, response);
    }
}
