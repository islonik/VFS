package org.vfs.core.network.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        Response response = new XmlResponse(code, specificCode, message);
        return response;
    }

    public Response parse(String request)
    {
        Response response;
        // TODO: should be dynamic in case of several protocols (xml, json ant e.t.c.)
        response = new XmlResponse();

        response.parse(request);

        return response;
    }
}
