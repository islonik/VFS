package org.vfs.core.network.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lipatov Nikita
 */
public class RequestService
{
    private static final Logger log = LoggerFactory.getLogger(RequestService.class);

    public Request create(String userId, String userLogin, String command)
    {
        User user = new User();
        user.setId(userId);
        user.setLogin(userLogin);

        Request request = new Request();
        request.setUser(user);
        request.setCommand(command);

        return request;
    }

    public Request parse(String xmlRequest)
    {
        XmlHelper xmlHelper = new XmlHelper();

        return (Request) xmlHelper.unmarshal(Request.class, xmlRequest);
    }

    public String toXml(Request request)
    {
        XmlHelper xmlHelper = new XmlHelper();
        return xmlHelper.marshal(Request.class, request);
    }
}