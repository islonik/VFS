package org.vfs.core.network.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vfs.core.network.protocol.impl.XmlRequest;

/**
 * @author Lipatov Nikita
 */
public class RequestFactory
{
    private static final Logger log = LoggerFactory.getLogger(RequestFactory.class);

    public Request create(String id, String login, String command)
    {
        // TODO: should be dynamic in case of several protocols (xml, json ant e.t.c.)
        Request request = new XmlRequest(id, login, command);
        return request;
    }

    public Request parse(String response)
    {
        Request request;
        // TODO: should be dynamic in case of several protocols (xml, json ant e.t.c.)
        request = new XmlRequest();

        request.parse(response);

        return request;
    }
}
