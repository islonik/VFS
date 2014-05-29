package org.vfs.core.network.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vfs.core.network.protocol.impl.UserElement;
import org.vfs.core.network.protocol.impl.XmlHelper;
import org.vfs.core.network.protocol.impl.XmlRequest;

/**
 * @author Lipatov Nikita
 */
public class RequestFactory
{
    private static final Logger log = LoggerFactory.getLogger(RequestFactory.class);

    public Request create(String userId, String userLogin, String command)
    {
        // TODO: should be dynamic in case of several protocols (xml, json ant e.t.c.)

        UserElement userElement = new UserElement();
        userElement.setId(userId);
        userElement.setLogin(userLogin);

        XmlRequest request = new XmlRequest();
        request.setUserElement(userElement);
        request.setCommand(command);

        return request;
    }

    public Request parse(String xmlRequest)
    {
        // TODO: should be dynamic in case of several protocols (xml, json ant e.t.c.)
        XmlHelper xmlHelper = new XmlHelper();

        return (Request) xmlHelper.unmarshal(XmlRequest.class, xmlRequest);
    }
}
