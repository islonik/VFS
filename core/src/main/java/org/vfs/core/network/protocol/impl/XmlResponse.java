package org.vfs.core.network.protocol.impl;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vfs.core.network.protocol.Response;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author Lipatov Nikita
 */
public class XmlResponse extends AbstractXml implements Response
{
    private static final Logger log = LoggerFactory.getLogger(XmlResponse.class);

    protected int code;
    protected long specificCode;
    protected String message;

    public XmlResponse(){}

    public XmlResponse(int code, long specificCode, String message)
    {
        this.code = code;
        this.message = message;

        document = new Document(new Element("response"));
        Element elementResponse = document.getRootElement();
        Element elementCode         = new Element("code");
        Element elementSpecificCode = new Element("specific_code");
        Element elementMessage      = new Element("message");

        elementResponse.addContent(elementCode);
        elementCode.setText(Integer.toString(this.code));

        elementResponse.addContent(elementMessage);
        elementMessage.setText(this.message);

        if(specificCode != -1)
        {
            elementResponse.addContent(elementSpecificCode);
            elementSpecificCode.setText(Long.toString(specificCode));
        }
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

            Element rootElement         = document.getRootElement();
            Element codeElement         = rootElement.getChild("code");
            Element specificCodeElement = rootElement.getChild("specific_code");
            Element messageElement      = rootElement.getChild("message");
            code = Integer.parseInt(codeElement.getText());
            message = messageElement.getText();
            if(specificCodeElement != null)
            {
                specificCode = Long.parseLong(specificCodeElement.getText());
            }
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

    public String getMessage()
    {
        return message;
    }

    public int getCode()
    {
        return code;
    }

    public long getSpecificCode()
    {
        return specificCode;
    }

}
