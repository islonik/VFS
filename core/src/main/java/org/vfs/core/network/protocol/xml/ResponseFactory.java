package org.vfs.core.network.protocol.xml;

/**
 * @author Lipatov Nikita
 */
public class ResponseFactory {

    private static XmlHelper xmlHelper = new XmlHelper();

    public static String newResponse(int status, String message)
    {
        Response response = new Response();
        response.setCode(status);
        response.setMessage(message);

        String xmlResponse = xmlHelper.marshal(Response.class, response);
        return xmlResponse;
    }

    public static String newResponse(int status, String message, String specificCode)
    {
        Response response = new Response();
        response.setCode(status);
        response.setMessage(message);
        response.setSpecificCode(specificCode);

        String xmlResponse = xmlHelper.marshal(Response.class, response);
        return xmlResponse;
    }
}
