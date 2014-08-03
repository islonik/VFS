package org.vfs.core.network.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.XMLConstants;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

/**
 * @author  Lipatov Nikita
 * Help link - http://dev64.wordpress.com/2012/03/19/xml-schema-jaxb-example/
 */
public class XmlHelper
{
    private static final Logger log = LoggerFactory.getLogger(XmlHelper.class);

    private Schema getSchema(String name) throws SAXException
    {
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        URL xsdURL = getClass().getResource("/schema/" + name + ".xsd");
        Schema schema = sf.newSchema(xsdURL);
        return schema;
    }

    public String marshal(Class className, Object object)
    {
        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(className);
            Marshaller marshaller = jaxbContext.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); // output pretty printed
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

            StringWriter writer = new StringWriter();
            marshaller.setSchema(getSchema(className.getSimpleName()));
            marshaller.marshal(object, writer);
            return writer.toString();
        }
        catch(JAXBException jaxbe)
        {
            log.error(jaxbe.getLocalizedMessage(), jaxbe);
            return null;
        }
        catch(SAXException saxe)
        {
            log.error(saxe.getLocalizedMessage(), saxe);
            return null;
        }
    }

    public <T> T unmarshal(Class<T> className, String xml)
    {
        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(className);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            jaxbUnmarshaller.setSchema(getSchema(className.getSimpleName()));
            return (T)jaxbUnmarshaller.unmarshal(new StringReader(xml));
        }
        catch(JAXBException jaxbe)
        {
            log.error(jaxbe.getLocalizedMessage(), jaxbe);
            return null;
        }
        catch(SAXException saxe)
        {
            log.error(saxe.getLocalizedMessage(), saxe);
            return null;
        }
    }

}
