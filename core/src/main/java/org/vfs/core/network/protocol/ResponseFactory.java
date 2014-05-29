package org.vfs.core.network.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vfs.core.network.protocol.impl.XmlHelper;
import org.vfs.core.network.protocol.impl.XmlResponse;

/**
 * @author Lipatov Nikita
 */
public class ResponseFactory
{

    private static final Logger log = LoggerFactory.getLogger(ResponseFactory.class);

    public Response create(int code, long specificCode, String message)
    {
        // TODO: should be dynamic in case of several protocols (xml, json ant e.t.c.)

        Response response = new XmlResponse();
        response.setMessage(message);
        response.setCode(Integer.toString(code));
        response.setSpecificCode(Long.toString(specificCode));

        return response;
    }

    public Response parse(String xmlResponse)
    {
        // TODO: should be dynamic in case of several protocols (xml, json ant e.t.c.)
        XmlHelper xmlHelper = new XmlHelper();

        return (Response) xmlHelper.unmarshal(XmlResponse.class, xmlResponse);
    }
}
