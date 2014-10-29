package org.vfs.core.network.protocol.xml;

/**
 * @author Lipatov Nikita
 */
public class RequestFactory
{
    private static XmlHelper xmlHelper = new XmlHelper();

    /*public static String newRequest(String userId, String userLogin, String command)
    {
        User user = new User();
        user.setId(userId);
        user.setLogin(userLogin);

        Request request = new Request();
        request.setUser(user);
        request.setCommand(command);

        String xmlResponse = xmlHelper.marshal(Request.class, request);
        return xmlResponse;
    }*/


}
