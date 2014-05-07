package org.vfs.core.network.protocol.impl;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vfs.core.network.protocol.Request;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author Lipatov Nikita
 */
public class XmlRequest extends AbstractXml implements Request
{
    private static final Logger log = LoggerFactory.getLogger(XmlRequest.class);

    protected String id;
    protected String login;
    protected String command;

    public XmlRequest(){}

    /**
     * @param id xml-data content.
     * @param login xml-data content.
     * @param command xml-data content.
     **/
    public XmlRequest(String id, String login, String command)
    {
        this.id = id;
        this.login = login;
        this.command = command;

        document = new Document(new Element("request"));
        Element elementRequest = document.getRootElement();
        Element elementUser    = new Element("user");
        Element elementCommand = new Element("command");
        elementRequest.addContent(elementUser);
        elementRequest.addContent(elementCommand);

        Attribute attributeId = new Attribute("id", this.id);
        elementUser.setAttribute(attributeId);

        Attribute attributeLogin = new Attribute("login", this.login);
        elementUser.setAttribute(attributeLogin);

        elementCommand.setText(this.command);
    }

    /**
     * @param data xml-data content.
     **/
    public void parse(String data)
    {
        try
        {
            SAXBuilder builder = new SAXBuilder();
            document = builder.build(new ByteArrayInputStream(data.getBytes()));

            Element rootElement    = document.getRootElement();
            Element userElement    = rootElement.getChild("user");
            Element commandElement = rootElement.getChild("command");
            id      = userElement.getAttributeValue("id");
            login   = userElement.getAttributeValue("login");
            command = commandElement.getText();
        }
        catch(IOException ioe)
        {
            log.error(ioe.getLocalizedMessage(), ioe);
        }
        catch(JDOMException jdome)
        {
            log.error(jdome.getLocalizedMessage(), jdome);
        }
    }

    public String getUserId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getUserLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public String getCommand()
    {
        return command;
    }

    public void setCommand(String command)
    {
        this.command = command;
    }

}
